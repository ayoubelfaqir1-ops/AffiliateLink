package ma.youcode.affiliatelink.offer.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    @Transactional
    public List<CampaignResponse> getCampaigns(CampaignStatus status) {
        List<Campaign> campaigns = (status != null)
                ? campaignRepository.findByStatus(status)
                : campaignRepository.findAll();
        return campaigns.stream().map(campaignMapper::toResponse).toList();
    }

    @Transactional
    public CampaignResponse updateStatus(Long id ,UpdateCampaignStatusRequest request){
        Campaign campaign = campaignRepository.findById(id)
                                            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        campaign.transitionTo(request.status(), LocalDate.now());
        return campaignMapper.toResponse(campaign);
    }
}
