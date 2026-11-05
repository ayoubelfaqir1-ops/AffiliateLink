package ma.youcode.affiliatelink.offer.dto;

import jakarta.validation.constraints.NotNull;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;

public record UpdateCampaignStatusRequest(@NotNull(message = "Status is required") CampaignStatus status) {
}

