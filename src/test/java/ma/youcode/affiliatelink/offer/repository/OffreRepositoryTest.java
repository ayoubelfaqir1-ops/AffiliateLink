package ma.youcode.affiliatelink.offer.repository;

import ma.youcode.affiliatelink.common.OffreSummaryDto;
import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.offer.entity.Campaign;
import ma.youcode.affiliatelink.offer.entity.Offre;
import ma.youcode.affiliatelink.offer.entity.Tag;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;
import ma.youcode.affiliatelink.support.AbstractPostgresContainerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository slice tests for {@link OffreRepository} and {@link OffreSpecification}.
 */
@DataJpaTest
@DisplayName("Offer Module: OffreRepository & Specification Slice Tests (@DataJpaTest)")
public class OffreRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private TagRepository tagRepository;

    private Tag createAndPersistTag(String name) {
        return tagRepository.save(new Tag(name));
    }

    private Offre createOffre(Long advertiserId, String title, String description, Niche niche) {
        return new Offre(advertiserId, title, description, "https://example.com", niche);
    }

    
    @Test
    @DisplayName("Specification: hasNiche - Should filter offers strictly by their niche")
    void shouldFilterByNiche() {
        // GIVEN: 1 TECH offer and 1 SPORT offer
        Offre techOffer = createOffre(1L, "Laptop Deal", "Best laptops", Niche.TECH);
        Offre sportOffer = createOffre(1L, "Running Shoes", "Best shoes", Niche.SPORT);

        offreRepository.save(techOffer);
        offreRepository.save(sportOffer);

        // WHEN: Query with hasNiche(TECH)
        List<Offre> results = offreRepository.findAll(OffreSpecification.hasNiche(Niche.TECH));

        // THEN: Only the TECH offer is returned
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Laptop Deal");
        assertThat(results.get(0).getNiche()).isEqualTo(Niche.TECH);
    }

    @Test
    @DisplayName("Specification: hasTag - Should perform JOIN on tags and filter correctly")
    void shouldFilterByTag() {
        // GIVEN: Tag 'blackfriday' attached to Offer 1 but not Offer 2
        Tag bfTag = createAndPersistTag("blackfriday");

        Offre offerWithTag = createOffre(1L, "BF Promo", "Special Promo", Niche.TECH);
        offerWithTag.addTag(bfTag);

        Offre offerWithoutTag = createOffre(1L, "Regular Offer", "Regular", Niche.TECH);

        offreRepository.save(offerWithTag);
        offreRepository.save(offerWithoutTag);

        // WHEN: Query with hasTag("blackfriday")
        List<Offre> results = offreRepository.findAll(OffreSpecification.hasTag("blackfriday"));

        // THEN: Only the tagged offer is returned
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("BF Promo");
    }

    @Test
    @DisplayName("Specification: containsKeyword - Should search case-insensitively in title and description")
    void shouldSearchKeywordInTitleAndDescription() {
        // GIVEN
        Offre offer1 = createOffre(1L, "Ultimate Smartphone Sale", "Affordable pricing", Niche.TECH);
        Offre offer2 = createOffre(1L, "Tablet Discount", "Great smartphone companion", Niche.TECH);
        Offre offer3 = createOffre(1L, "Headphones Deal", "Noise cancelling audio", Niche.TECH);

        offreRepository.save(offer1);
        offreRepository.save(offer2);
        offreRepository.save(offer3);

        // WHEN: Search keyword 'SMARTPHONE' (case-insensitive)
        List<Offre> results = offreRepository.findAll(OffreSpecification.containsKeyword("SMARTPHONE"));

        // THEN: offer1 (title match) and offer2 (description match) returned
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Offre::getTitle)
                .containsExactlyInAnyOrder("Ultimate Smartphone Sale", "Tablet Discount");
    }

    @Test
    @DisplayName("Specification: Combined Filters - Niche + Tag + Keyword")
    void shouldCombineMultipleSpecifications() {
        // GIVEN
        Tag summerTag = createAndPersistTag("summer");

        Offre match = createOffre(1L, "Summer Smartwatch", "Waterproof fitness tracker", Niche.SPORT);
        match.addTag(summerTag);

        Offre wrongNiche = createOffre(1L, "Summer Smartwatch", "Waterproof fitness tracker", Niche.TECH);
        wrongNiche.addTag(summerTag);

        Offre missingTag = createOffre(1L, "Summer Smartwatch", "Waterproof fitness tracker", Niche.SPORT);

        offreRepository.save(match);
        offreRepository.save(wrongNiche);
        offreRepository.save(missingTag);

        // WHEN: Combine specifications
        Specification<Offre> spec = Specification
                .where(OffreSpecification.hasNiche(Niche.SPORT))
                .and(OffreSpecification.hasTag("summer"))
                .and(OffreSpecification.containsKeyword("smartwatch"));

        List<Offre> results = offreRepository.findAll(spec);

        // THEN: Only the exact match is returned
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(match.getId());
    }

    @Test
    @DisplayName("Cascade ALL: Persisting an Offre automatically persists attached campaigns")
    void shouldCascadePersistCampaigns() {
        // GIVEN: Offre with an attached Campaign
        Offre offre = createOffre(1L, "Cascade Offer", "Description", Niche.BEAUTY);
        Campaign campaign = new Campaign(
                "Attached Campaign",
                LocalDate.now(),
                LocalDate.now().plusWeeks(2),
                new BigDecimal("500.00"),
                PayoutModel.CPC,
                new BigDecimal("0.50")
        );
        offre.addCampaign(campaign);

        // WHEN: Save parent offer
        Offre savedOffre = offreRepository.save(offre);

        // THEN: Campaign was assigned an ID via CascadeType.ALL
        assertThat(savedOffre.getCampaigns()).hasSize(1);
        assertThat(savedOffre.getCampaigns().get(0).getId()).isNotNull();
        assertThat(savedOffre.getCampaigns().get(0).getName()).isEqualTo("Attached Campaign");
    }
}
