package ma.youcode.affiliatelink.profile.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.dto.AdvertiserResponse;
import ma.youcode.affiliatelink.profile.dto.CreateAdvertiserRequest;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
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

    @Transactional
    public AdvertiserResponse createAdvertiser(CreateAdvertiserRequest request) {
        if (profileRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email " + request.email() + " is already in use");
        }

        Advertiser advertiser = new Advertiser(
            request.email(),
            request.companyName(),
            request.sector(),
            request.website()
        );

        Advertiser savedAdvertiser = advertiserRepository.save(advertiser);
        return AdvertiserResponse.from(savedAdvertiser);
    }

    public AdvertiserResponse getAdvertiserById(Long id) {
        return advertiserRepository.findById(id)
            .map(AdvertiserResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("Advertiser not found with id: " + id));
    }

    public List<AdvertiserResponse> getAllAdvertisers() {
        return advertiserRepository.findAll()
            .stream()
            .map(AdvertiserResponse::from)
            .toList();
    }
}
