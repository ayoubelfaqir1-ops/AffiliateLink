package ma.youcode.affiliatelink.offer.repository;

import ma.youcode.affiliatelink.offer.entity.Campaign;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStatus(CampaignStatus status);
    Optional<Campaign> findByOffreIdAndStatus(Long offerId, CampaignStatus status);
    List<Campaign> findByOffreAdvertiserIdAndStatus(Long advertiserId, CampaignStatus status);
}

