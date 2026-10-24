package ma.youcode.affiliatelink.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.youcode.affiliatelink.profile.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByEmail(String email);
}