package ma.youcode.affiliatelink.profile.repository;

import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.Profile;
import ma.youcode.affiliatelink.support.AbstractPostgresContainerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DisplayName("Profile Module: ProfileRepository Slice Tests (@DataJpaTest)")
class ProfileRepositoryTest extends AbstractPostgresContainerTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @DisplayName("Should return true when email exists in database")
    void shouldReturnTrueWhenEmailExists() {
        // GIVEN: Save a profile directly to H2
        Profile advertiser = new Advertiser("existing@corp.com", "Acme", "Tech", "https://acme.com");
        profileRepository.save(advertiser);

        // WHEN: Query existsByEmail
        boolean exists = profileRepository.existsByEmail("existing@corp.com");

        // THEN: Must be true
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist in database")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // WHEN
        boolean exists = profileRepository.existsByEmail("notfound@corp.com");

        // THEN
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should fail with DataIntegrityViolationException on duplicate email")
    void shouldThrowExceptionWhenDuplicateEmailPersisted() {
        // GIVEN: Persist first profile
        Profile profile1 = new Advertiser("duplicate@corp.com", "Company A", "Retail", "https://a.com");
        profileRepository.saveAndFlush(profile1);

        // WHEN: Attempt to persist second profile with the identical email
        Profile profile2 = new Advertiser("duplicate@corp.com", "Company B", "Finance", "https://b.com");

        // THEN: Database unique constraint enforces uniqueness at SQL level
        assertThatThrownBy(() -> profileRepository.saveAndFlush(profile2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

