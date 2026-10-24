package ma.youcode.affiliatelink.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_advertisers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertiser extends Profile {

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "sector", nullable = false, length = 100)
    private String sector;

    @Column(name = "website", nullable = false, length = 255)
    private String website;

    public Advertiser(String email, String companyName, String sector, String website) {
        super(email);
        this.companyName = companyName;
        this.sector = sector;
        this.website = website;
    }
}
