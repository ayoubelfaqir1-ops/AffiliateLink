package ma.youcode.affiliatelink.offer.mapper;

import ma.youcode.affiliatelink.offer.api.CampaignDto;
import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatelink.offer.entity.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    CampaignResponse toResponse(Campaign campaign);

    Campaign toEntity(CreateCampaignRequest request);

    @Mapping(target = "offerId", source = "offre.id")
    CampaignDto toDto(Campaign campaign);
}
