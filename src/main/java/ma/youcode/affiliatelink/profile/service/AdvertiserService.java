package ma.youcode.affiliatelink.profile.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.dto.AdvertiserResponse;
import ma.youcode.affiliatelink.profile.dto.CreateAdvertiserRequest;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.mapper.AdvertiserMapper;
import ma.youcode.affiliatelink.profile.repository.AdvertiserRepository;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertiserService {

    private final AdvertiserRepository advertiserRepository;
    private final ProfileRepository profileRepository;
    private final AdvertiserMapper advertiserMapper;

    @Transactional
    public AdvertiserResponse createAdvertiser(CreateAdvertiserRequest request) {
        if (profileRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email " + request.email() + " is already in use");
        }

        Advertiser advertiser = advertiserMapper.toEntity(request);
        Advertiser savedAdvertiser = advertiserRepository.save(advertiser);
        return advertiserMapper.toResponse(savedAdvertiser);
    }

    public AdvertiserResponse getAdvertiserById(Long id) {
        return advertiserRepository.findById(id)
            .map(advertiserMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Advertiser not found with id: " + id));
    }

    public List<AdvertiserResponse> getAllAdvertisers() {
        return advertiserRepository.findAll()
            .stream()
            .map(advertiserMapper::toResponse)
            .toList();
    }
}
