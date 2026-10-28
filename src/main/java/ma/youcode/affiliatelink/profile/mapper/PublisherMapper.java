package ma.youcode.affiliatelink.profile.mapper;

import ma.youcode.affiliatelink.profile.api.PublisherDto;
import ma.youcode.affiliatelink.profile.dto.CreatePublisherRequest;
import ma.youcode.affiliatelink.profile.dto.PublisherResponse;
import ma.youcode.affiliatelink.profile.entity.Publisher;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    PublisherResponse toResponse(Publisher publisher);

    @Mapping(target = "fullName", source = "publisher", qualifiedByName = "combineNames")
    @Mapping(target = "isActive", source = "status")
    PublisherDto toDto(Publisher publisher);

    Publisher toEntity(CreatePublisherRequest request);

    @Named("combineNames")
    default String combineNames(Publisher publisher) {
        if (publisher == null) {
            return null;
        }
        return publisher.getFirstName() + " " + publisher.getLastName();
    }

    default boolean toIsActive(ProfileStatus status) {
        return status == ProfileStatus.ACTIVE;
    }
}

