package ma.youcode.affiliatelink.offer.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.enums.Niche;
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
        // 1. Verify advertiser exists across module boundary via API
        advertiserApi.getAdvertiserById(request.advertiserId());

        // 2. Map basic fields
        Offre offre = offreMapper.toEntity(request);

        // 3. Resolve or create tags
        if (request.tags() != null) {
            for (String tagName : request.tags()) {
                String normalized = tagName.trim().toLowerCase();
                Tag tag = tagRepository.findByName(normalized)
                    .orElseGet(() -> tagRepository.save(new Tag(normalized)));
                offre.addTag(tag);
            }
        }

        // 4. Save and return response DTO
        Offre saved = offreRepository.save(offre);
        return offreMapper.toResponse(saved);
    }

    public OffreResponse getOffreById(Long id) {
        return offreRepository.findById(id)
            .map(offreMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));
    }

    @Transactional
    public OffreResponse updateOffre(Long id, UpdateOffreRequest request) {
        // 1. Find the offer
        Offre offre = offreRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));
        
        // 2. Update offer attributes
        offre.update(request.title(), request.description(), request.landingUrl(), request.niche());

        // 3. Clear tags and re-add the new tags
        offre.clearTags();
        Set<Tag> newTags = resolveTags(request.tags());
        newTags.forEach(offre::addTag);
    
        // 4. Return response (dirty checking handles the SQL UPDATE automatically)
        return offreMapper.toResponse(offre);
    }

    @Transactional 
    public CampaignResponse createCampaign(Long id, CreateCampaignRequest request) {
        // 1. Find the offer
        Offre offre = offreRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));
        
        // 2. Map request to Campaign entity
        Campaign campaign = campaignMapper.toEntity(request);

        // 3. Establish bidirectional relationship
        offre.addCampaign(campaign);
    
        // 4. Persist to generate IDENTITY primary key immediately
        Campaign savedCampaign = campaignRepository.save(campaign);

        // 5. Return response DTO with generated id
        return campaignMapper.toResponse(savedCampaign);
    }

    public Page<OffreResponse> searchOffres(Niche niche, Long advertiserId, String tag, String keyword, Pageable pageable) {
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
