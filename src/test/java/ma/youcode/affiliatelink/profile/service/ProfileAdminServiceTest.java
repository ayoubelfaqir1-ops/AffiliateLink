package ma.youcode.affiliatelink.profile.service;

import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.api.AdvertiserSuspended;
import ma.youcode.affiliatelink.profile.dto.UpdateProfileStatusRequest;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.Publisher;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import ma.youcode.affiliatelink.support.TestDataFactory;
import ma.youcode.affiliatelink.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Profile Module: ProfileAdminService Unit Tests")
class ProfileAdminServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ApplicationEventPublisher events;

    @InjectMocks
    private ProfileAdminService profileAdminService;

    @Nested
    @DisplayName("updateProfileStatus tests")
    class UpdateProfileStatusTests {

        @Test
        @DisplayName("Should activate pending profile and not publish suspended event")
        void shouldActivatePendingProfile() {
            // GIVEN: Profile starts in PENDING status
            Advertiser profile = TestDataFactory.anAdvertiser();
            assertThat(profile.getStatus()).isEqualTo(ProfileStatus.PENDING);

            when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

            // WHEN: Transition to ACTIVE
            profileAdminService.updateProfileStatus(1L, new UpdateProfileStatusRequest(ProfileStatus.ACTIVE));

            // THEN: Status is updated, no suspended event published
            assertThat(profile.getStatus()).isEqualTo(ProfileStatus.ACTIVE);
            verify(profileRepository).save(profile);
            verify(events, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should publish AdvertiserSuspended event when Advertiser status changes to SUSPENDED")
        void shouldPublishEventWhenAdvertiserStatusSuspended() {
            // GIVEN: Active advertiser
            Advertiser profile = TestDataFactory.anAdvertiser();
            profile.changeStatus(ProfileStatus.ACTIVE);

            when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

            // WHEN: Transition to SUSPENDED
            profileAdminService.updateProfileStatus(1L, new UpdateProfileStatusRequest(ProfileStatus.SUSPENDED));

            // THEN: Status is SUSPENDED and event is published
            assertThat(profile.getStatus()).isEqualTo(ProfileStatus.SUSPENDED);
            verify(profileRepository).save(profile);

            ArgumentCaptor<AdvertiserSuspended> captor = ArgumentCaptor.forClass(AdvertiserSuspended.class);
            verify(events).publishEvent(captor.capture());
            assertThat(captor.getValue().email()).isEqualTo(profile.getEmail());
            assertThat(captor.getValue().advertiserId()).isEqualTo(profile.getId());
        }

        @Test
        @DisplayName("Should NOT publish AdvertiserSuspended event when Publisher status changes to SUSPENDED")
        void shouldNotPublishEventWhenPublisherStatusSuspended() {
            // GIVEN: Active publisher
            Publisher profile = TestDataFactory.aPublisher();
            profile.changeStatus(ProfileStatus.ACTIVE);

            when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

            // WHEN: Transition to SUSPENDED
            profileAdminService.updateProfileStatus(1L, new UpdateProfileStatusRequest(ProfileStatus.SUSPENDED));

            // THEN: Status is SUSPENDED but no event is published
            assertThat(profile.getStatus()).isEqualTo(ProfileStatus.SUSPENDED);
            verify(profileRepository).save(profile);

            verify(events, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when transition is illegal")
        void shouldThrowWhenIllegalTransition() {
            // GIVEN: Profile in PENDING status cannot transition directly to SUSPENDED
            Advertiser profile = TestDataFactory.anAdvertiser();
            when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

            // WHEN & THEN
            assertThatThrownBy(() -> profileAdminService.updateProfileStatus(1L, new UpdateProfileStatusRequest(ProfileStatus.SUSPENDED)))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not allowed");

            verify(events, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when profile not found")
        void shouldThrowWhenNotFound() {
            when(profileRepository.findById(TestFixtures.NON_EXISTING_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> profileAdminService.updateProfileStatus(TestFixtures.NON_EXISTING_ID, new UpdateProfileStatusRequest(ProfileStatus.ACTIVE)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}

