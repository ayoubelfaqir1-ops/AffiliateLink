package ma.youcode.affiliatelink.offer.entity.enums;

import java.math.BigDecimal;

public enum PayoutModel {
    CPC,
    CPL,
    CPA_FIXED,
    CPA_PERCENT;

    public boolean isValidPayoutValue(BigDecimal value) {
        if (value == null) return false;
        if (this == CPA_PERCENT) {
            return value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(new BigDecimal("100")) <= 0;
        }
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
}
