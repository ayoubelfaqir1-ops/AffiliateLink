package ma.youcode.affiliatelink.offer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ma.youcode.affiliatelink.common.enums.Niche;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "offer_offres")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertiser_id", nullable = false)
    private Long advertiserId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "landing_url", nullable = false, length = 255)
    private String landingUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "niche", nullable = false, length = 50)
    private Niche niche;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Campaign> campaigns = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "offer_tags_offres",
        joinColumns = @JoinColumn(name = "offer_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Offre(Long advertiserId, String title, String description, String landingUrl, Niche niche) {
        this.advertiserId = advertiserId;
        this.title = title;
        this.description = description;
        this.landingUrl = landingUrl;
        this.niche = niche;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String description, String landingUrl, Niche niche) {
        this.title = title;
        this.description = description;
        this.landingUrl = landingUrl;
        this.niche = niche;
    }

    public void addCampaign(Campaign campaign) {
        this.campaigns.add(campaign);
        campaign.setOffre(this);
    }

    public void removeCampaign(Campaign campaign) {
        this.campaigns.remove(campaign);
        campaign.setOffre(null);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getOffres().add(this);
    }

    public void clearTags() {
        this.tags.clear();
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getOffres().remove(this);
    }
}
