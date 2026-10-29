package ma.youcode.affiliatelink.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ma.youcode.affiliatelink.common.enums.Niche;

@Entity
@Table(name = "profile_publishers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Publisher extends Profile {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "niche", nullable = false, length = 20)
    private Niche niche;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    public Publisher(String email, String firstName, String lastName, Niche niche, String bio, String country) {
        super(email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.niche = niche;
        this.bio = bio;
        this.country = country;
    }
}

