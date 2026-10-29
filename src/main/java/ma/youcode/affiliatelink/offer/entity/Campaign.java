package ma.youcode.affiliatelink.offer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offer_campaigns")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "budget", nullable = false, precision = 12, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_model", nullable = false, length = 30)
    private PayoutModel payoutModel;

    @Column(name = "payout_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal payoutValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CampaignStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Offre offre;

    public Campaign(String name, LocalDate startDate, LocalDate endDate, BigDecimal budget,
                    PayoutModel payoutModel, BigDecimal payoutValue) {
        validateDates(startDate, endDate);
        validateBudget(budget);
        validatePayout(payoutModel, payoutValue);

        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.payoutModel = payoutModel;
        this.payoutValue = payoutValue;
        this.status = CampaignStatus.DRAFT;
    }

    public void transitionTo(CampaignStatus target, LocalDate today) {
        if (!this.status.canTransitionTo(target)) {
            throw new BusinessRuleException("Cannot transition campaign from " + this.status + " to " + target);
        }

        if (target == CampaignStatus.ACTIVE && this.endDate.isBefore(today)) {
            throw new BusinessRuleException("Cannot activate a campaign whose end date (" + this.endDate + ") is in the past");
        }

        this.status = target;
    }

    public void pause() {
        if (!this.status.canTransitionTo(CampaignStatus.PAUSED)) {
            throw new BusinessRuleException("Cannot pause campaign in state: " + this.status);
        }
        this.status = CampaignStatus.PAUSED;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessRuleException("Start date and end date are required");
        }
        if (!endDate.isAfter(startDate)) {
            throw new BusinessRuleException("Campaign end date must be strictly after start date");
        }
    }

    private void validateBudget(BigDecimal budget) {
        if (budget == null || budget.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Campaign budget must be strictly positive");
        }
    }

    private void validatePayout(PayoutModel payoutModel, BigDecimal payoutValue) {
        if (payoutModel == null || payoutValue == null) {
            throw new BusinessRuleException("Payout model and payout value are required");
        }
        if (!payoutModel.isValidPayoutValue(payoutValue)) {
            throw new BusinessRuleException("Invalid payout value " + payoutValue + " for model " + payoutModel);
        }
    }
}
