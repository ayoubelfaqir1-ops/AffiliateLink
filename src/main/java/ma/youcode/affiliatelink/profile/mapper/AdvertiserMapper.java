package ma.youcode.affiliatelink.profile.mapper;

import ma.youcode.affiliatelink.profile.api.AdvertiserDto;
import ma.youcode.affiliatelink.profile.dto.AdvertiserResponse;
import ma.youcode.affiliatelink.profile.dto.CreateAdvertiserRequest;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdvertiserMapper {

    AdvertiserResponse toResponse(Advertiser advertiser);

    @Mapping(target = "isActive", source = "status")
    AdvertiserDto toDto(Advertiser advertiser);

    Advertiser toEntity(CreateAdvertiserRequest request);

    default boolean toIsActive(ProfileStatus status) {
        return status == ProfileStatus.ACTIVE;
    }
}

