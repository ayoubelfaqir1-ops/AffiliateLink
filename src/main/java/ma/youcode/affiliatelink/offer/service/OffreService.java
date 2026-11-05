package ma.youcode.affiliatelink.offer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatelink.offer.dto.CreateOffreRequest;
import ma.youcode.affiliatelink.offer.dto.OffreResponse;
import ma.youcode.affiliatelink.offer.dto.UpdateOffreRequest;
import ma.youcode.affiliatelink.offer.entity.Campaign;
import ma.youcode.affiliatelink.offer.entity.Offre;
import ma.youcode.affiliatelink.offer.entity.Tag;
import ma.youcode.affiliatelink.offer.mapper.CampaignMapper;
import ma.youcode.affiliatelink.offer.mapper.OffreMapper;
import ma.youcode.affiliatelink.offer.repository.CampaignRepository;
import ma.youcode.affiliatelink.offer.repository.OffreRepository;
import ma.youcode.affiliatelink.offer.repository.OffreSpecification;
import ma.youcode.affiliatelink.offer.repository.TagRepository;
import ma.youcode.affiliatelink.profile.api.AdvertiserApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OffreService {

    private final OffreRepository offreRepository;
    private final CampaignRepository campaignRepository;
    private final OffreMapper offreMapper;
    private final CampaignMapper campaignMapper;
    private final TagRepository tagRepository;
    private final AdvertiserApi advertiserApi;

    @Transactional
    public OffreResponse create(CreateOffreRequest request) {
        log.info("Creating offer for advertiser ID: {}", request.advertiserId());

        // 1. Verify advertiser exists across module boundary via API
        advertiserApi.getAdvertiserById(request.advertiserId());

        // 2. Business Rule: Only ACTIVE advertisers can create offers
        if (!advertiserApi.isStatusActive(request.advertiserId())) {
            log.warn("Offer creation rejected: Advertiser {} is not ACTIVE", request.advertiserId());
            throw new BusinessRuleException("Advertiser with id " + request.advertiserId() + " is not ACTIVE");
        }

        // 3. Map basic fields
        Offre offre = offreMapper.toEntity(request);

        // 4. Resolve or create tags
        if (request.tags() != null) {
            for (String tagName : request.tags()) {
                String normalized = tagName.trim().toLowerCase();
                Tag tag = tagRepository.findByName(normalized)
                    .orElseGet(() -> tagRepository.save(new Tag(normalized)));
                offre.addTag(tag);
            }
        }

        // 5. Save and return response DTO
        Offre saved = offreRepository.save(offre);
        log.info("Successfully created offer with ID: {}", saved.getId());
        return offreMapper.toResponse(saved);
    }

    public OffreResponse getOffreById(Long id) {
        log.debug("Fetching offer with ID: {}", id);
        return offreRepository.findById(id)
            .map(offreMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));
    }

    @Transactional
    public OffreResponse updateOffre(Long id, UpdateOffreRequest request) {
        log.info("Updating offer with ID: {}", id);
        Offre offre = offreRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));
        
        offre.update(request.title(), request.description(), request.landingUrl(), request.niche());

        offre.clearTags();
        Set<Tag> newTags = resolveTags(request.tags());
        newTags.forEach(offre::addTag);
    
        log.info("Successfully updated offer ID: {}", id);
        return offreMapper.toResponse(offre);
    }

    @Transactional 
    public CampaignResponse createCampaign(Long id, CreateCampaignRequest request) {
        log.info("Creating campaign for offer ID: {}", id);
        Offre offre = offreRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));

        if (!advertiserApi.isStatusActive(offre.getAdvertiserId())) {
            log.warn("Campaign creation rejected: Advertiser {} is not ACTIVE", offre.getAdvertiserId());
            throw new BusinessRuleException("Cannot create campaign: Advertiser " + offre.getAdvertiserId() + " is not ACTIVE");
        }
        
        Campaign campaign = campaignMapper.toEntity(request);
        offre.addCampaign(campaign);
        Campaign savedCampaign = campaignRepository.save(campaign);

        log.info("Successfully created campaign ID: {} for offer ID: {}", savedCampaign.getId(), id);
        return campaignMapper.toResponse(savedCampaign);
    }

    public Page<OffreResponse> searchOffres(Niche niche, Long advertiserId, String tag, String keyword, Pageable pageable) {
        log.debug("Searching offers with niche={}, advertiserId={}, tag={}, keyword={}", niche, advertiserId, tag, keyword);
        Specification<Offre> spec = Specification
            .where(OffreSpecification.hasNiche(niche))
            .and(OffreSpecification.hasAdvertiserId(advertiserId))
            .and(OffreSpecification.hasTag(tag))
            .and(OffreSpecification.containsKeyword(keyword));

        return offreRepository.findAll(spec, pageable)
            .map(offreMapper::toResponse);
    }

    private Set<Tag> resolveTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Set.of();
        }
        Set<Tag> resolvedTags = new HashSet<>();
        for (String tagName : tagNames) {
            String normalized = tagName.trim().toLowerCase();
            Tag tag = tagRepository.findByName(normalized)
                .orElseGet(() -> tagRepository.save(new Tag(normalized)));
            resolvedTags.add(tag);
        }
        return resolvedTags;
    }
}
