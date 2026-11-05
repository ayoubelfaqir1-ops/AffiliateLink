package ma.youcode.affiliatelink.offer.mapper;

import ma.youcode.affiliatelink.offer.dto.CreateOffreRequest;
import ma.youcode.affiliatelink.offer.dto.OffreResponse;
import ma.youcode.affiliatelink.offer.entity.Offre;
import ma.youcode.affiliatelink.offer.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CampaignMapper.class})
public interface OffreMapper {

    OffreResponse toResponse(Offre offre);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Offre toEntity(CreateOffreRequest request);

    default String mapTag(Tag tag) {
        return tag != null ? tag.getName() : null;
    }
}
