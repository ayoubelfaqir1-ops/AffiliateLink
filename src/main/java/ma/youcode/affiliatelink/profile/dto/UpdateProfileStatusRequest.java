package ma.youcode.affiliatelink.profile.dto;

import jakarta.validation.constraints.NotNull;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;

public record UpdateProfileStatusRequest(
    @NotNull(message = "Target status is required")
    ProfileStatus status
) {}
