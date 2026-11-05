package ma.youcode.affiliatelink.offer.api;

import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignDto(
    Long id,
    Long offerId,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal budget,
    PayoutModel payoutModel,
    BigDecimal payoutValue,
    CampaignStatus status
) {}
