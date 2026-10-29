package ma.youcode.affiliatelink.offer.dto;

import ma.youcode.affiliatelink.common.enums.Niche;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record OffreResponse(
    Long id,
    Long advertiserId,
    String title,
    String description,
    String landingUrl,
    Niche niche,
    LocalDateTime createdAt,
    Set<String> tags,
    List<CampaignResponse> campaigns
) {}
