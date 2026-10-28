package ma.youcode.affiliatelink.profile;

import ma.youcode.affiliatelink.profile.api.ProfileSuspended;
import ma.youcode.affiliatelink.profile.dto.UpdateProfileStatusRequest;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.Profile;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import ma.youcode.affiliatelink.profile.service.ProfileAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.PublishedEvents;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
public class ProfileModuleVerificationTest {

    @Autowired
    private ProfileAdminService profileService;
    @Autowired
    private ProfileRepository profileRepository; // Inject your repository

    @BeforeEach
    void setUp() {
        // Clear database and insert a test profile so ID 1 exists
        profileRepository.deleteAll();

        Profile profile = new Advertiser("ayoub@email.com", "Company Name", "Technology", "https://example.com");

        profileRepository.save(profile); // This will assign ID 1 (or match your test ID)
    }
    @Test
    void shouldPublishProfileSuspendedEventWhenProfileStatusIsSuspended(PublishedEvents events) {
        // 1. Act: Call your service method
        Long testProfileId = 1L;
        profileService.updateProfileStatus(testProfileId, new UpdateProfileStatusRequest(ProfileStatus.ACTIVE));
        profileService.updateProfileStatus(testProfileId, new UpdateProfileStatusRequest(ProfileStatus.SUSPENDED));

        // 2. Assert: Verify the event was published with correct data
        assertThat(events.ofType(ProfileSuspended.class))
                .hasSize(1)
                .singleElement().satisfies(event -> event.id().equals(testProfileId));
    }
}

