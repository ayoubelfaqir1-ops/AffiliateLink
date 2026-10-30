package ma.youcode.affiliatelink.offer.repository;

import ma.youcode.affiliatelink.offer.entity.Campaign;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStatus(CampaignStatus status);
}

