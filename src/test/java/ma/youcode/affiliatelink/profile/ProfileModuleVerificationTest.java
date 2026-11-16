package ma.youcode.affiliatelink.profile;

import ma.youcode.affiliatelink.profile.api.AdvertiserSuspended;
import ma.youcode.affiliatelink.profile.dto.UpdateProfileStatusRequest;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.Profile;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import ma.youcode.affiliatelink.profile.service.ProfileAdminService;
import ma.youcode.affiliatelink.support.AbstractPostgresContainerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.PublishedEvents;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
public class ProfileModuleVerificationTest extends AbstractPostgresContainerTest {

    @Autowired
    private ProfileAdminService profileService;
    @Autowired
    private ProfileRepository profileRepository;

    private Long testProfileId;

    @BeforeEach
    void setUp() {
        Profile profile = new Advertiser("ayoub@email.com", "Company Name", "Technology", "https://example.com");

        Profile saved = profileRepository.save(profile);
        this.testProfileId = saved.getId();
    }
    @Test
    void shouldPublishAdvertiserSuspendedEventWhenAdvertiserStatusIsSuspended(PublishedEvents events) {
        // 1. Act: Call your service method
        profileService.updateProfileStatus(testProfileId, new UpdateProfileStatusRequest(ProfileStatus.ACTIVE));
        profileService.updateProfileStatus(testProfileId, new UpdateProfileStatusRequest(ProfileStatus.SUSPENDED));

        // 2. Assert: Verify the event was published with correct data
        assertThat(events.ofType(AdvertiserSuspended.class))
                .hasSize(1)
                .singleElement().satisfies(event -> event.advertiserId().equals(testProfileId));
    }
}

