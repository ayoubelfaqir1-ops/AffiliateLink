package ma.youcode.affiliatelink.profile.api;

public record PublisherDto(
    Long id,
    String email,
    String fullName,
    boolean isActive
) {}
