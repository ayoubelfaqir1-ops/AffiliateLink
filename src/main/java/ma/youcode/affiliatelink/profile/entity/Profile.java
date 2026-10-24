package ma.youcode.affiliatelink.profile.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_profiles")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileStatus status = ProfileStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Profile(String email) {
        this.email = email;
    }

    public void changeStatus(ProfileStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new BusinessRuleException(
                "Transition " + status + " -> " + target + " is not allowed");
        }
        this.status = target;
    }

}
