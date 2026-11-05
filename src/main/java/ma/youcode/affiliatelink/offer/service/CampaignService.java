package ma.youcode.affiliatelink.offer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.UpdateCampaignStatusRequest;
import ma.youcode.affiliatelink.offer.entity.Campaign;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.mapper.CampaignMapper;
import ma.youcode.affiliatelink.offer.repository.CampaignRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    public List<CampaignResponse> getCampaigns(CampaignStatus status) {
        log.debug("Fetching campaigns with status filter: {}", status);
        List<Campaign> campaigns = (status != null)
                ? campaignRepository.findByStatus(status)
                : campaignRepository.findAll();
        return campaigns.stream().map(campaignMapper::toResponse).toList();
    }

    @Transactional
    public CampaignResponse updateStatus(Long id, UpdateCampaignStatusRequest request) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        CampaignStatus targetStatus = request.status();
        log.info("Transitioning campaign ID: {} from {} to {}", id, campaign.getStatus(), targetStatus);

        if (targetStatus == CampaignStatus.ACTIVE) {
            Long offerId = campaign.getOffre().getId();

            campaignRepository.findByOffreIdAndStatus(offerId, CampaignStatus.ACTIVE)
                    .filter(existing -> !existing.getId().equals(campaign.getId()))
                    .ifPresent(existingActive -> {
                        log.info("Auto-pausing previously active campaign ID: {} on offer ID: {}", existingActive.getId(), offerId);
                        existingActive.pause();
                    });
        }

        campaign.transitionTo(targetStatus, LocalDate.now());
        log.info("Successfully updated campaign ID: {} to status: {}", id, targetStatus);
        return campaignMapper.toResponse(campaign);
    }

    @Transactional
    public void pauseActiveCampaignsForAdvertiser(Long advertiserId) {
        log.info("Pausing all active campaigns for suspended advertiser ID: {}", advertiserId);
        List<Campaign> campaigns = campaignRepository.findByOffreAdvertiserIdAndStatus(advertiserId,
                CampaignStatus.ACTIVE);
        campaigns.forEach(campaign -> {
            log.info("Pausing campaign ID: {} for suspended advertiser ID: {}", campaign.getId(), advertiserId);
            campaign.pause();
        });
        log.info("Paused {} active campaign(s) for advertiser ID: {}", campaigns.size(), advertiserId);
    }
}
