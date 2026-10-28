package ma.youcode.affiliatelink.profile.api;

public record AdvertiserDto(
    Long id,
    String email,
    String companyName,
    boolean isActive
) {}
