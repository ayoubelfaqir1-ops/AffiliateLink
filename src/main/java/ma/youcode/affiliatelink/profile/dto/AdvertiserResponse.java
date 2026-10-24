package ma.youcode.affiliatelink.profile.dto;

import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;

import java.time.LocalDateTime;

public record AdvertiserResponse(
    Long id,
    String email,
    ProfileStatus status,
    LocalDateTime createdAt,
    String companyName,
    String sector,
    String website
) {
    public static AdvertiserResponse from(Advertiser advertiser) {
        return new AdvertiserResponse(
            advertiser.getId(),
            advertiser.getEmail(),
            advertiser.getStatus(),
            advertiser.getCreatedAt(),
            advertiser.getCompanyName(),
            advertiser.getSector(),
            advertiser.getWebsite()
        );
    }
}
