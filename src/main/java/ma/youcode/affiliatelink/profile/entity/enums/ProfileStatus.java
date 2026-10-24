package ma.youcode.affiliatelink.profile.entity.enums;

public enum ProfileStatus {
    PENDING, ACTIVE, SUSPENDED;

    public boolean canTransitionTo(ProfileStatus target) {
        return switch (this) {
            case PENDING   -> target == ACTIVE;
            case ACTIVE    -> target == SUSPENDED;
            case SUSPENDED -> target == ACTIVE;
        };
    }
}
