package ma.youcode.affiliatelink.profile;

import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.profile.dto.AdvertiserResponse;
import ma.youcode.affiliatelink.profile.dto.CreateAdvertiserRequest;
import ma.youcode.affiliatelink.profile.dto.CreatePublisherRequest;
import ma.youcode.affiliatelink.profile.dto.PublisherResponse;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import ma.youcode.affiliatelink.profile.service.AdvertiserService;
import ma.youcode.affiliatelink.profile.service.PublisherService;
import ma.youcode.affiliatelink.support.AbstractPostgresContainerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("Profile Module: Integration Tests (@SpringBootTest)")
class ProfileIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private AdvertiserService advertiserService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @DisplayName("Should persist and retrieve an advertiser end-to-end")
    void shouldPersistAndRetrieveAdvertiser() {
        // GIVEN
        CreateAdvertiserRequest request = new CreateAdvertiserRequest(
                "corp@adtech.com",
                "AdTech Global",
                "Marketing",
                "https://adtech.com"
        );

        // WHEN
        AdvertiserResponse response = advertiserService.createAdvertiser(request);

        // THEN
        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo("corp@adtech.com");
        assertThat(response.status()).isEqualTo(ProfileStatus.PENDING);

        AdvertiserResponse fetched = advertiserService.getAdvertiserById(response.id());
        assertThat(fetched.companyName()).isEqualTo("AdTech Global");
    }

    @Test
    @DisplayName("Should persist and retrieve a publisher end-to-end")
    void shouldPersistAndRetrievePublisher() {
        // GIVEN
        CreatePublisherRequest request = new CreatePublisherRequest(
                "creator@channel.com",
                "Karim",
                "Alami",
                Niche.GAMING,
                "Gaming streamer",
                "Morocco"
        );

        // WHEN
        PublisherResponse response = publisherService.createPublisher(request);

        // THEN
        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo("creator@channel.com");

        PublisherResponse fetched = publisherService.getPublisherById(response.id());
        assertThat(fetched.firstName()).isEqualTo("Karim");
        assertThat(fetched.niche()).isEqualTo(Niche.GAMING);
    }

    @Test
    @DisplayName("Should reject duplicate email across profile types")
    void shouldRejectDuplicateEmailAcrossProfiles() {
        // GIVEN: Existing advertiser with this email
        advertiserService.createAdvertiser(new CreateAdvertiserRequest(
                "shared@domain.com",
                "Brand X",
                "Retail",
                "https://brandx.com"
        ));

        // WHEN: Attempt to register publisher with the same email
        CreatePublisherRequest duplicateRequest = new CreatePublisherRequest(
                "shared@domain.com",
                "Duplicate",
                "User",
                Niche.TECH,
                "Bio",
                "Morocco"
        );

        // THEN: BusinessRuleException is thrown
        assertThatThrownBy(() -> publisherService.createPublisher(duplicateRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already in use");
    }
}

