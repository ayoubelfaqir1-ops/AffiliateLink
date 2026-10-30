package ma.youcode.affiliatelink.offer.dto;

import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;

public record UpdateCampaignStatusRequest(CampaignStatus status) {
}

