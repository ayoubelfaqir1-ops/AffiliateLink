package ma.youcode.affiliatelink.offer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCampaignRequest(
    @NotBlank(message = "Campaign name is required")
    @Size(max = 150, message = "Campaign name must not exceed 150 characters")
    String name,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.01", message = "Budget must be strictly positive")
    BigDecimal budget,

    @NotNull(message = "Payout model is required")
    PayoutModel payoutModel,

    @NotNull(message = "Payout value is required")
    @DecimalMin(value = "0.01", message = "Payout value must be strictly positive")
    BigDecimal payoutValue
) {}
