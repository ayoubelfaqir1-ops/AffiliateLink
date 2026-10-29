package ma.youcode.affiliatelink.offer.mapper;

import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatelink.offer.entity.Campaign;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    CampaignResponse toResponse(Campaign campaign);

    Campaign toEntity(CreateCampaignRequest request);
}
