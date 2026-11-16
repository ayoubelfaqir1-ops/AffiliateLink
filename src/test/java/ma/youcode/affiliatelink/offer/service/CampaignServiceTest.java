package ma.youcode.affiliatelink.offer.service;

import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.UpdateCampaignStatusRequest;
import ma.youcode.affiliatelink.offer.entity.Campaign;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;
import ma.youcode.affiliatelink.offer.mapper.CampaignMapper;
import ma.youcode.affiliatelink.offer.repository.CampaignRepository;
import ma.youcode.affiliatelink.support.TestDataFactory;
import ma.youcode.affiliatelink.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Offer Module: CampaignService Unit Tests")
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private CampaignMapper campaignMapper;

    @InjectMocks
    private CampaignService campaignService;

    @Nested
    @DisplayName("getCampaigns tests")
    class GetCampaignsTests {

        @Test
        @DisplayName("Should return all campaigns when status filter is null")
        void shouldReturnAllCampaignsWhenFilterNull() {
            Campaign campaign = TestDataFactory.aCampaign();
            CampaignResponse response = new CampaignResponse(
                    1L,
                    TestFixtures.CAMPAIGN_NAME,
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(30),
                    new BigDecimal("5000.00"),
                    PayoutModel.CPC,
                    new BigDecimal("1.50"),
                    CampaignStatus.DRAFT
            );

            when(campaignRepository.findAll()).thenReturn(List.of(campaign));
            when(campaignMapper.toResponse(campaign)).thenReturn(response);

            List<CampaignResponse> result = campaignService.getCampaigns(null);

            assertThat(result).hasSize(1);
            verify(campaignRepository).findAll();
            verify(campaignRepository, never()).findByStatus(any());
        }

        @Test
        @DisplayName("Should return filtered campaigns when status is provided")
        void shouldReturnFilteredCampaigns() {
            Campaign campaign = TestDataFactory.aCampaign();
            CampaignResponse response = new CampaignResponse(
                    1L,
                    TestFixtures.CAMPAIGN_NAME,
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(30),
                    new BigDecimal("5000.00"),
                    PayoutModel.CPC,
                    new BigDecimal("1.50"),
                    CampaignStatus.ACTIVE
            );

            when(campaignRepository.findByStatus(CampaignStatus.ACTIVE)).thenReturn(List.of(campaign));
            when(campaignMapper.toResponse(campaign)).thenReturn(response);

            List<CampaignResponse> result = campaignService.getCampaigns(CampaignStatus.ACTIVE);

            assertThat(result).hasSize(1);
            verify(campaignRepository).findByStatus(CampaignStatus.ACTIVE);
            verify(campaignRepository, never()).findAll();
        }
    }

    @Nested
    @DisplayName("pauseActiveCampaignsForAdvertiser tests")
    class PauseActiveCampaignsTests {

        @Test
        @DisplayName("Should pause all active campaigns for a suspended advertiser")
        void shouldPauseActiveCampaigns() {
            Long advertiserId = 1L;
            Campaign campaign = TestDataFactory.aCampaign();
            // Transition to ACTIVE so pause() is valid
            campaign.transitionTo(CampaignStatus.ACTIVE, LocalDate.now());
            assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.ACTIVE);

            when(campaignRepository.findByOffreAdvertiserIdAndStatus(advertiserId, CampaignStatus.ACTIVE))
                    .thenReturn(List.of(campaign));

            campaignService.pauseActiveCampaignsForAdvertiser(advertiserId);

            assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.PAUSED);
            verify(campaignRepository).findByOffreAdvertiserIdAndStatus(advertiserId, CampaignStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("updateStatus tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campaign does not exist")
        void shouldThrowWhenNotFound() {
            when(campaignRepository.findById(TestFixtures.NON_EXISTING_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> campaignService.updateStatus(TestFixtures.NON_EXISTING_ID, new UpdateCampaignStatusRequest(CampaignStatus.ACTIVE)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}

