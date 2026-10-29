package ma.youcode.affiliatelink.offer.repository;

import ma.youcode.affiliatelink.offer.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}

