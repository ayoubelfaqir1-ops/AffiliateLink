package ma.youcode.affiliatelink.profile.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.api.AdvertiserSuspended;
import ma.youcode.affiliatelink.profile.dto.UpdateProfileStatusRequest;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.Profile;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileAdminService {

    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher events;

    @Transactional
    public void updateProfileStatus(Long id, UpdateProfileStatusRequest request) {
        Profile profile = profileRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));

        profile.changeStatus(request.status());
        profileRepository.save(profile);
        if (request.status() == ProfileStatus.SUSPENDED && profile instanceof Advertiser) {
            events.publishEvent(new AdvertiserSuspended(profile.getId(), profile.getEmail()));
        }
    }
}
