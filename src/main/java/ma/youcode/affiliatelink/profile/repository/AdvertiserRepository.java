package ma.youcode.affiliatelink.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.youcode.affiliatelink.profile.entity.Advertiser;

public interface AdvertiserRepository extends JpaRepository<Advertiser,Long>{
    
}
