package ma.youcode.affiliatelink.profile.dto;

import ma.youcode.affiliatelink.profile.entity.Publisher;
import ma.youcode.affiliatelink.profile.entity.enums.Niche;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;

import java.time.LocalDateTime;

public record PublisherResponse(
    Long id,
    String email,
    ProfileStatus status,
    LocalDateTime createdAt,
    String firstName,
    String lastName,
    Niche niche,
    String bio,
    String country
) {
    public static PublisherResponse from(Publisher publisher) {
        return new PublisherResponse(
            publisher.getId(),
            publisher.getEmail(),
            publisher.getStatus(),
            publisher.getCreatedAt(),
            publisher.getFirstName(),
            publisher.getLastName(),
            publisher.getNiche(),
            publisher.getBio(),
            publisher.getCountry()
        );
    }
}
