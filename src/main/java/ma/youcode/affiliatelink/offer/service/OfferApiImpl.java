package ma.youcode.affiliatelink.offer.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.offer.api.CampaignDto;
import ma.youcode.affiliatelink.offer.api.OfferApi;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.mapper.CampaignMapper;
import ma.youcode.affiliatelink.offer.repository.CampaignRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfferApiImpl implements OfferApi {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    @Override
    public CampaignDto getCampaignById(Long id) {
        return campaignRepository.findById(id)
            .map(campaignMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
    }

    @Override
    public boolean isCampaignActive(Long id) {
        return campaignRepository.findById(id)
            .map(campaign -> campaign.getStatus() == CampaignStatus.ACTIVE)
            .orElse(false);
    }
}
