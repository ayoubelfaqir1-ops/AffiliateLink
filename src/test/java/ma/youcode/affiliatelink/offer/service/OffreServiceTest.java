package ma.youcode.affiliatelink.offer.service;

import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.offer.dto.CreateOffreRequest;
import ma.youcode.affiliatelink.offer.dto.OffreResponse;
import ma.youcode.affiliatelink.offer.entity.Offre;
import ma.youcode.affiliatelink.offer.entity.Tag;
import ma.youcode.affiliatelink.offer.mapper.CampaignMapper;
import ma.youcode.affiliatelink.offer.mapper.OffreMapper;
import ma.youcode.affiliatelink.offer.repository.CampaignRepository;
import ma.youcode.affiliatelink.offer.repository.OffreRepository;
import ma.youcode.affiliatelink.offer.repository.TagRepository;
import ma.youcode.affiliatelink.profile.api.AdvertiserApi;
import ma.youcode.affiliatelink.profile.api.AdvertiserDto;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Offer Module: OffreService Unit Tests")
class OffreServiceTest {

    @Mock
    private OffreRepository offreRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private OffreMapper offreMapper;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private AdvertiserApi advertiserApi;

    @InjectMocks
    private OffreService offreService;

    @Nested
    @DisplayName("create tests")
    class CreateTests {

        @Test
        @DisplayName("Should create offer when advertiser is active")
        void shouldCreateOfferWhenAdvertiserIsActive() {
            // GIVEN
            Long advertiserId = 1L;
            CreateOffreRequest request = new CreateOffreRequest(
                    advertiserId,
                    "Winter Discount",
                    "Description",
                    "https://store.example.com",
                    Niche.TECH,
                    Set.of("tech", "deals")
            );
            Offre entity = new Offre(advertiserId, "Winter Discount", "Description", "https://store.example.com", Niche.TECH);
            Offre savedEntity = new Offre(advertiserId, "Winter Discount", "Description", "https://store.example.com", Niche.TECH);
            OffreResponse response = new OffreResponse(
                    1L,
                    advertiserId,
                    "Winter Discount",
                    "Description",
                    "https://store.example.com",
                    Niche.TECH,
                    LocalDateTime.now(),
                    Set.of("tech", "deals"),
                    List.of()
            );

            when(advertiserApi.getAdvertiserById(advertiserId)).thenReturn(new AdvertiserDto(advertiserId, "adv@test.com", "Acme", true));
            when(advertiserApi.isStatusActive(advertiserId)).thenReturn(true);
            when(offreMapper.toEntity(request)).thenReturn(entity);
            when(tagRepository.findByName(any())).thenReturn(Optional.empty());
            when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(offreRepository.save(entity)).thenReturn(savedEntity);
            when(offreMapper.toResponse(savedEntity)).thenReturn(response);

            // WHEN
            OffreResponse result = offreService.create(request);

            // THEN
            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("Winter Discount");
            verify(advertiserApi).isStatusActive(advertiserId);
            verify(offreRepository).save(entity);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when advertiser is not active")
        void shouldThrowWhenAdvertiserNotActive() {
            // GIVEN
            Long advertiserId = 2L;
            CreateOffreRequest request = new CreateOffreRequest(
                    advertiserId,
                    "Summer Deal",
                    "Description",
                    "https://store.example.com",
                    Niche.SPORT,
                    Set.of()
            );

            when(advertiserApi.getAdvertiserById(advertiserId)).thenReturn(new AdvertiserDto(advertiserId, "adv@test.com", "Acme", false));
            when(advertiserApi.isStatusActive(advertiserId)).thenReturn(false);

            // WHEN & THEN
            assertThatThrownBy(() -> offreService.create(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("is not ACTIVE");

            verify(offreRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getOffreById tests")
    class GetOffreByIdTests {

        @Test
        @DisplayName("Should throw ResourceNotFoundException when offer does not exist")
        void shouldThrowWhenNotFound() {
            when(offreRepository.findById(TestFixtures.NON_EXISTING_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> offreService.getOffreById(TestFixtures.NON_EXISTING_ID))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}

