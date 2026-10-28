package ma.youcode.affiliatelink.profile.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.api.AdvertiserApi;
import ma.youcode.affiliatelink.profile.api.AdvertiserDto;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.mapper.AdvertiserMapper;
import ma.youcode.affiliatelink.profile.repository.AdvertiserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertiserApiImpl implements AdvertiserApi {

    private final AdvertiserRepository advertiserRepository;
    private final AdvertiserMapper advertiserMapper;

    @Override
    public AdvertiserDto getAdvertiserById(Long id) {
        return advertiserRepository.findById(id)
            .map(advertiserMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Advertiser not found with id: " + id));
    }

    @Override
    public boolean isStatusActive(Long id) {
        return advertiserRepository.findById(id)
            .map(advertiser -> advertiser.getStatus() == ProfileStatus.ACTIVE)
            .orElse(false);
    }
}
