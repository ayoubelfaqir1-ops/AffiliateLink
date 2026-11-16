package ma.youcode.affiliatelink.offer.repository;

import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.offer.entity.Campaign;
import ma.youcode.affiliatelink.offer.entity.Offre;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;
import ma.youcode.affiliatelink.support.AbstractPostgresContainerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Repository slice tests for {@link CampaignRepository}.
 */
@DataJpaTest
@DisplayName("Offer Module: CampaignRepository Slice Tests (@DataJpaTest)")
public class CampaignRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private OffreRepository offreRepository;

    // --- Helper methods to create test fixtures ---

    private Offre createAndPersistOffre(Long advertiserId, String title) {
        Offre offre = new Offre(
                advertiserId,
                title,
                "Description for " + title,
                "https://example.com/promo",
                Niche.TECH
        );
        return offreRepository.save(offre);
    }

    private Campaign buildCampaign(String name, CampaignStatus targetStatus) {
        Campaign campaign = new Campaign(
                name,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("1000.00"),
                PayoutModel.CPA_FIXED,
                new BigDecimal("15.00")
        );
        if (targetStatus == CampaignStatus.ACTIVE) {
            campaign.transitionTo(CampaignStatus.ACTIVE, LocalDate.now());
        }
        return campaign;
    }

    // --- Tests ---

    @Test
    @DisplayName("findByStatus - Should return only campaigns matching the given status")
    void shouldFindCampaignsByStatus() {
        // GIVEN: 1 Offre with 1 ACTIVE campaign and 1 DRAFT campaign
        Offre offre = createAndPersistOffre(1L, "Tech Gadgets");

        Campaign activeCampaign = buildCampaign("Active Promo", CampaignStatus.ACTIVE);
        Campaign draftCampaign = buildCampaign("Draft Promo", CampaignStatus.DRAFT);

        offre.addCampaign(activeCampaign);
        offre.addCampaign(draftCampaign);
        offreRepository.save(offre);

        // WHEN: Query for ACTIVE campaigns
        List<Campaign> activeCampaigns = campaignRepository.findByStatus(CampaignStatus.ACTIVE);

        // THEN: Only the ACTIVE campaign is returned
        assertThat(activeCampaigns).hasSize(1);
        assertThat(activeCampaigns.get(0).getName()).isEqualTo("Active Promo");
        assertThat(activeCampaigns.get(0).getStatus()).isEqualTo(CampaignStatus.ACTIVE);
    }

    @Test
    @DisplayName("findByOffreIdAndStatus - Should return campaign matching both specific offer and status")
    void shouldFindCampaignByOffreIdAndStatus() {
        // GIVEN: 2 separate offers, each having an ACTIVE campaign
        Offre offre1 = createAndPersistOffre(1L, "Offer 1");
        Offre offre2 = createAndPersistOffre(1L, "Offer 2");

        Campaign campaignOffer1 = buildCampaign("Campaign for Offer 1", CampaignStatus.ACTIVE);
        Campaign campaignOffer2 = buildCampaign("Campaign for Offer 2", CampaignStatus.ACTIVE);

        offre1.addCampaign(campaignOffer1);
        offre2.addCampaign(campaignOffer2);

        offreRepository.save(offre1);
        offreRepository.save(offre2);

        // WHEN: Query specifically for Offer 1's ACTIVE campaign
        Optional<Campaign> result = campaignRepository.findByOffreIdAndStatus(offre1.getId(), CampaignStatus.ACTIVE);

        // THEN: Returns only campaign for Offer 1
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Campaign for Offer 1");
        assertThat(result.get().getOffre().getId()).isEqualTo(offre1.getId());
    }

    @Test
    @DisplayName("findByOffreAdvertiserIdAndStatus - Should navigate relationship and return campaigns by advertiser ID")
    void shouldFindCampaignsByOffreAdvertiserIdAndStatus() {
        // GIVEN:
        // Advertiser 10 has an offer with 1 ACTIVE campaign
        Offre offreAdv10 = createAndPersistOffre(10L, "Advertiser 10 Offer");
        Campaign campaignAdv10 = buildCampaign("Adv10 Promo", CampaignStatus.ACTIVE);
        offreAdv10.addCampaign(campaignAdv10);
        offreRepository.save(offreAdv10);

        // Advertiser 20 has an offer with 1 ACTIVE campaign
        Offre offreAdv20 = createAndPersistOffre(20L, "Advertiser 20 Offer");
        Campaign campaignAdv20 = buildCampaign("Adv20 Promo", CampaignStatus.ACTIVE);
        offreAdv20.addCampaign(campaignAdv20);
        offreRepository.save(offreAdv20);

        // WHEN: Query campaigns for Advertiser 10 with status ACTIVE
        List<Campaign> results = campaignRepository.findByOffreAdvertiserIdAndStatus(10L, CampaignStatus.ACTIVE);

        // THEN: Only campaigns belonging to Advertiser 10 are returned
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Adv10 Promo");
        assertThat(results.get(0).getOffre().getAdvertiserId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Enforce Foreign Key: Saving a Campaign without an Offre must fail")
    void shouldFailWhenCampaignPersistedWithoutOffre() {
        // GIVEN: A campaign with no assigned Offre (offre is null)
        Campaign orphanCampaign = buildCampaign("Orphan", CampaignStatus.DRAFT);

        // WHEN & THEN: Database constraint rejects null foreign key
        assertThatThrownBy(() -> campaignRepository.saveAndFlush(orphanCampaign))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
