package ma.youcode.affiliatelink.offer;

import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatelink.offer.dto.CreateOffreRequest;
import ma.youcode.affiliatelink.offer.dto.OffreResponse;
import ma.youcode.affiliatelink.offer.dto.UpdateCampaignStatusRequest;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;
import ma.youcode.affiliatelink.offer.service.CampaignService;
import ma.youcode.affiliatelink.offer.service.OffreService;
import ma.youcode.affiliatelink.profile.dto.AdvertiserResponse;
import ma.youcode.affiliatelink.profile.dto.CreateAdvertiserRequest;
import ma.youcode.affiliatelink.profile.dto.UpdateProfileStatusRequest;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.service.AdvertiserService;
import ma.youcode.affiliatelink.profile.service.ProfileAdminService;
import ma.youcode.affiliatelink.support.AbstractPostgresContainerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for the Offer module.
 */
@SpringBootTest
@Transactional
@DisplayName("Offer Module: Service Integration Tests (@SpringBootTest)")
class OfferIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private OffreService offreService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private AdvertiserService advertiserService;

    @Autowired
    private ProfileAdminService profileAdminService;

    // --- Helper to set up an ACTIVE advertiser in real PostgreSQL ---
    private Long createActiveAdvertiser(String email, String company) {
        AdvertiserResponse created = advertiserService.createAdvertiser(
                new CreateAdvertiserRequest(email, company, "Tech", "https://example.com")
        );
        profileAdminService.updateProfileStatus(created.id(), new UpdateProfileStatusRequest(ProfileStatus.ACTIVE));
        return created.id();
    }

    @Test
    @DisplayName("Should successfully create offer and campaign end-to-end via service layer")
    void shouldCreateOfferAndCampaignViaService() {
        // GIVEN: An active advertiser registered in DB
        Long advertiserId = createActiveAdvertiser("active.adv@tech.com", "Active Adv Corp");

        CreateOffreRequest offerRequest = new CreateOffreRequest(
                advertiserId,
                "Cyber Monday Special",
                "Best cloud hosting deals",
                "https://cloud.example.com",
                Niche.TECH,
                Set.of("cloud", "saas")
        );

        // WHEN: Create offer via OffreService
        OffreResponse offerResponse = offreService.create(offerRequest);

        // THEN: Offer is persisted with generated ID and mapped DTO
        assertThat(offerResponse.id()).isNotNull();
        assertThat(offerResponse.title()).isEqualTo("Cyber Monday Special");
        assertThat(offerResponse.advertiserId()).isEqualTo(advertiserId);
        assertThat(offerResponse.tags()).contains("cloud", "saas");

        // WHEN: Create campaign attached to this offer via OffreService
        CreateCampaignRequest campaignRequest = new CreateCampaignRequest(
                "Winter Q4 Campaign",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(30),
                new BigDecimal("5000.00"),
                PayoutModel.CPA_FIXED,
                new BigDecimal("25.00")
        );

        CampaignResponse campaignResponse = offreService.createCampaign(offerResponse.id(), campaignRequest);

        // THEN: Campaign is created in DRAFT status
        assertThat(campaignResponse.id()).isNotNull();
        assertThat(campaignResponse.name()).isEqualTo("Winter Q4 Campaign");
        assertThat(campaignResponse.status()).isEqualTo(CampaignStatus.DRAFT);
    }

    @Test
    @DisplayName("Should reject offer creation if advertiser is PENDING (not ACTIVE)")
    void shouldRejectOfferCreationWhenAdvertiserIsNotActive() {
        // GIVEN: Advertiser in PENDING status (default on registration)
        AdvertiserResponse pendingAdv = advertiserService.createAdvertiser(
                new CreateAdvertiserRequest("pending@brand.com", "Pending Brand", "Retail", "https://brand.com")
        );

        CreateOffreRequest request = new CreateOffreRequest(
                pendingAdv.id(),
                "Invalid Offer",
                "This should fail",
                "https://example.com",
                Niche.LIFESTYLE,
                Set.of()
        );

        // WHEN & THEN: OffreService checks AdvertiserApi and rejects with BusinessRuleException
        assertThatThrownBy(() -> offreService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("is not ACTIVE");
    }

    @Test
    @DisplayName("Auto-Pause Rule: Activating a campaign should auto-pause existing active campaign on same offer")
    void shouldAutoPauseExistingActiveCampaignWhenNewOneActivated() {
        // GIVEN: An active advertiser and an offer
        Long advertiserId = createActiveAdvertiser("autopilot@brand.com", "Auto Brand");
        OffreResponse offer = offreService.create(new CreateOffreRequest(
                advertiserId,
                "Multi Campaign Offer",
                "Test Auto Pause",
                "https://example.com",
                Niche.LIFESTYLE,
                Set.of()
        ));

        // Create Campaign 1 and activate it
        CampaignResponse campaign1 = offreService.createCampaign(offer.id(), new CreateCampaignRequest(
                "Campaign 1",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                new BigDecimal("1000.00"),
                PayoutModel.CPC,
                new BigDecimal("1.00")
        ));
        campaignService.updateStatus(campaign1.id(), new UpdateCampaignStatusRequest(CampaignStatus.ACTIVE));

        // Create Campaign 2 (starts in DRAFT)
        CampaignResponse campaign2 = offreService.createCampaign(offer.id(), new CreateCampaignRequest(
                "Campaign 2",
                LocalDate.now(),
                LocalDate.now().plusDays(20),
                new BigDecimal("2000.00"),
                PayoutModel.CPC,
                new BigDecimal("2.00")
        ));

        // WHEN: Activate Campaign 2
        CampaignResponse activatedCampaign2 = campaignService.updateStatus(
                campaign2.id(),
                new UpdateCampaignStatusRequest(CampaignStatus.ACTIVE)
        );

        // THEN: Campaign 2 is ACTIVE
        assertThat(activatedCampaign2.status()).isEqualTo(CampaignStatus.ACTIVE);

        // AND THEN: Campaign 1 was automatically transitioned to PAUSED
        CampaignResponse reFetchedCampaign1 = campaignService.getCampaigns(CampaignStatus.PAUSED).stream()
                .filter(c -> c.id().equals(campaign1.id()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Campaign 1 should be PAUSED"));

        assertThat(reFetchedCampaign1.status()).isEqualTo(CampaignStatus.PAUSED);
    }
}
