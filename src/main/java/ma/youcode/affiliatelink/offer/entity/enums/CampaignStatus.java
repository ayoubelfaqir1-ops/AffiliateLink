package ma.youcode.affiliatelink.offer.entity.enums;

public enum CampaignStatus {
    DRAFT,
    ACTIVE,
    PAUSED,
    ENDED;

    public boolean canTransitionTo(CampaignStatus target) {
        if (target == null) return false;
        return switch (this) {
            case DRAFT   -> target == ACTIVE;
            case ACTIVE  -> target == PAUSED || target == ENDED;
            case PAUSED  -> target == ACTIVE || target == ENDED;
            case ENDED   -> false;
        };
    }
}
