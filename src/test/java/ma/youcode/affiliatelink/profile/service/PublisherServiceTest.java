package ma.youcode.affiliatelink.profile.service;

import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.dto.CreatePublisherRequest;
import ma.youcode.affiliatelink.profile.dto.PublisherResponse;
import ma.youcode.affiliatelink.profile.entity.Publisher;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.mapper.PublisherMapper;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import ma.youcode.affiliatelink.profile.repository.PublisherRepository;
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
@DisplayName("Profile Module: PublisherService Unit Tests")
class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PublisherMapper publisherMapper;

    @InjectMocks
    private PublisherService publisherService;

    @Nested
    @DisplayName("createPublisher tests")
    class CreatePublisherTests {

        @Test
        @DisplayName("Should create publisher successfully when email is unique")
        void shouldCreatePublisherWhenEmailIsUnique() {
            // GIVEN
            CreatePublisherRequest request = new CreatePublisherRequest(
                    TestFixtures.PUBLISHER_EMAIL,
                    "Sara",
                    "Benali",
                    Niche.TECH,
                    "Tech blogger",
                    "Morocco"
            );
            Publisher entity = TestDataFactory.aPublisher();
            Publisher savedEntity = TestDataFactory.aPublisher();
            PublisherResponse expectedResponse = new PublisherResponse(
                    1L,
                    TestFixtures.PUBLISHER_EMAIL,
                    ProfileStatus.PENDING,
                    LocalDateTime.now(),
                    "Sara",
                    "Benali",
                    Niche.TECH,
                    "Tech blogger",
                    "Morocco"
            );

            when(profileRepository.existsByEmail(request.email())).thenReturn(false);
            when(publisherMapper.toEntity(request)).thenReturn(entity);
            when(publisherRepository.save(entity)).thenReturn(savedEntity);
            when(publisherMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

            // WHEN
            PublisherResponse response = publisherService.createPublisher(request);

            // THEN
            assertThat(response).isNotNull();
            assertThat(response.email()).isEqualTo(TestFixtures.PUBLISHER_EMAIL);
            verify(profileRepository).existsByEmail(request.email());
            verify(publisherRepository).save(entity);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            // GIVEN
            CreatePublisherRequest request = new CreatePublisherRequest(
                    TestFixtures.PUBLISHER_EMAIL,
                    "Sara",
                    "Benali",
                    Niche.TECH,
                    "Bio",
                    "Morocco"
            );
            when(profileRepository.existsByEmail(request.email())).thenReturn(true);

            // WHEN & THEN
            assertThatThrownBy(() -> publisherService.createPublisher(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("already in use");

            verify(publisherRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getPublisherById tests")
    class GetPublisherByIdTests {

        @Test
        @DisplayName("Should return publisher when ID exists")
        void shouldReturnPublisherWhenIdExists() {
            // GIVEN
            Long id = 1L;
            Publisher publisher = TestDataFactory.aPublisher();
            PublisherResponse expectedResponse = new PublisherResponse(
                    id,
                    TestFixtures.PUBLISHER_EMAIL,
                    ProfileStatus.PENDING,
                    LocalDateTime.now(),
                    "Sara",
                    "Benali",
                    Niche.TECH,
                    "Bio",
                    "Morocco"
            );

            when(publisherRepository.findById(id)).thenReturn(Optional.of(publisher));
            when(publisherMapper.toResponse(publisher)).thenReturn(expectedResponse);

            // WHEN
            PublisherResponse response = publisherService.getPublisherById(id);

            // THEN
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(id);
            verify(publisherRepository).findById(id);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void shouldThrowWhenIdNotFound() {
            // GIVEN
            Long unknownId = TestFixtures.NON_EXISTING_ID;
            when(publisherRepository.findById(unknownId)).thenReturn(Optional.empty());

            // WHEN & THEN
            assertThatThrownBy(() -> publisherService.getPublisherById(unknownId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found with id: " + unknownId);
        }
    }

    @Nested
    @DisplayName("getAllPublishers tests")
    class GetAllPublishersTests {

        @Test
        @DisplayName("Should return list of all publishers")
        void shouldReturnAllPublishers() {
            // GIVEN
            Publisher publisher = TestDataFactory.aPublisher();
            PublisherResponse responseDto = new PublisherResponse(
                    1L,
                    TestFixtures.PUBLISHER_EMAIL,
                    ProfileStatus.PENDING,
                    LocalDateTime.now(),
                    "Sara",
                    "Benali",
                    Niche.TECH,
                    "Bio",
                    "Morocco"
            );

            when(publisherRepository.findAll()).thenReturn(List.of(publisher));
            when(publisherMapper.toResponse(publisher)).thenReturn(responseDto);

            // WHEN
            List<PublisherResponse> result = publisherService.getAllPublishers();

            // THEN
            assertThat(result).hasSize(1);
            assertThat(result.get(0).firstName()).isEqualTo("Sara");
            verify(publisherRepository).findAll();
        }
    }
}

