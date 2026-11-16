package ma.youcode.affiliatelink.profile.service;

import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.dto.AdvertiserResponse;
import ma.youcode.affiliatelink.profile.dto.CreateAdvertiserRequest;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.mapper.AdvertiserMapper;
import ma.youcode.affiliatelink.profile.repository.AdvertiserRepository;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import ma.youcode.affiliatelink.support.TestDataFactory;
import ma.youcode.affiliatelink.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Profile Module: AdvertiserService Unit Tests")
class AdvertiserServiceTest {

    @Mock
    private AdvertiserRepository advertiserRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private AdvertiserMapper advertiserMapper;

    @InjectMocks
    private AdvertiserService advertiserService;

    @Nested
    @DisplayName("createAdvertiser tests")
    class CreateAdvertiserTests {

        @Test
        @DisplayName("Should create advertiser successfully when email is unique")
        void shouldCreateAdvertiserWhenEmailIsUnique() {
            // GIVEN
            CreateAdvertiserRequest request = new CreateAdvertiserRequest(
                    TestFixtures.ADVERTISER_EMAIL,
                    TestFixtures.COMPANY_NAME,
                    "Technology",
                    "https://acme.example.com"
            );
            Advertiser entity = TestDataFactory.anAdvertiser();
            Advertiser savedEntity = TestDataFactory.anAdvertiser();
            AdvertiserResponse expectedResponse = new AdvertiserResponse(
                    1L,
                    TestFixtures.ADVERTISER_EMAIL,
                    ProfileStatus.PENDING,
                    LocalDateTime.now(),
                    TestFixtures.COMPANY_NAME,
                    "Technology",
                    "https://acme.example.com"
            );

            when(profileRepository.existsByEmail(request.email())).thenReturn(false);
            when(advertiserMapper.toEntity(request)).thenReturn(entity);
            when(advertiserRepository.save(entity)).thenReturn(savedEntity);
            when(advertiserMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

            // WHEN
            AdvertiserResponse response = advertiserService.createAdvertiser(request);

            // THEN
            assertThat(response).isNotNull();
            assertThat(response.email()).isEqualTo(TestFixtures.ADVERTISER_EMAIL);
            verify(profileRepository).existsByEmail(request.email());
            verify(advertiserRepository).save(entity);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            // GIVEN
            CreateAdvertiserRequest request = new CreateAdvertiserRequest(
                    TestFixtures.ADVERTISER_EMAIL,
                    TestFixtures.COMPANY_NAME,
                    "Tech",
                    "https://test.com"
            );
            when(profileRepository.existsByEmail(request.email())).thenReturn(true);

            // WHEN & THEN
            assertThatThrownBy(() -> advertiserService.createAdvertiser(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("already in use");

            verify(advertiserRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getAdvertiserById tests")
    class GetAdvertiserByIdTests {

        @Test
        @DisplayName("Should return advertiser when ID exists")
        void shouldReturnAdvertiserWhenIdExists() {
            // GIVEN
            Long id = 1L;
            Advertiser advertiser = TestDataFactory.anAdvertiser();
            AdvertiserResponse expectedResponse = new AdvertiserResponse(
                    id,
                    TestFixtures.ADVERTISER_EMAIL,
                    ProfileStatus.PENDING,
                    LocalDateTime.now(),
                    TestFixtures.COMPANY_NAME,
                    "Technology",
                    "https://acme.example.com"
            );

            when(advertiserRepository.findById(id)).thenReturn(Optional.of(advertiser));
            when(advertiserMapper.toResponse(advertiser)).thenReturn(expectedResponse);

            // WHEN
            AdvertiserResponse response = advertiserService.getAdvertiserById(id);

            // THEN
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(id);
            verify(advertiserRepository).findById(id);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void shouldThrowWhenIdNotFound() {
            // GIVEN
            Long unknownId = TestFixtures.NON_EXISTING_ID;
            when(advertiserRepository.findById(unknownId)).thenReturn(Optional.empty());

            // WHEN & THEN
            assertThatThrownBy(() -> advertiserService.getAdvertiserById(unknownId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found with id: " + unknownId);
        }
    }

    @Nested
    @DisplayName("getAllAdvertisers tests")
    class GetAllAdvertisersTests {

        @Test
        @DisplayName("Should return list of all advertisers")
        void shouldReturnAllAdvertisers() {
            // GIVEN
            Advertiser advertiser = TestDataFactory.anAdvertiser();
            AdvertiserResponse responseDto = new AdvertiserResponse(
                    1L,
                    TestFixtures.ADVERTISER_EMAIL,
                    ProfileStatus.PENDING,
                    LocalDateTime.now(),
                    TestFixtures.COMPANY_NAME,
                    "Tech",
                    "https://test.com"
            );

            when(advertiserRepository.findAll()).thenReturn(List.of(advertiser));
            when(advertiserMapper.toResponse(advertiser)).thenReturn(responseDto);

            // WHEN
            List<AdvertiserResponse> result = advertiserService.getAllAdvertisers();

            // THEN
            assertThat(result).hasSize(1);
            assertThat(result.get(0).companyName()).isEqualTo(TestFixtures.COMPANY_NAME);
            verify(advertiserRepository).findAll();
        }
    }
}

