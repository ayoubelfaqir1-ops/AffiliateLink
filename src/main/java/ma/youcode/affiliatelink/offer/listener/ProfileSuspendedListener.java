package ma.youcode.affiliatelink.offer.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.youcode.affiliatelink.offer.service.CampaignService;
import ma.youcode.affiliatelink.profile.api.ProfileSuspended;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileSuspendedListener {

    private final CampaignService campaignService;

    @ApplicationModuleListener
    public void onProfileSuspended(ProfileSuspended event) {
        log.info("Received ProfileSuspended event for advertiser ID: {}. Processing campaign pauses.", event.id());
        campaignService.pauseActiveCampaignsForAdvertiser(event.id());
        log.info("Finished processing ProfileSuspended event for advertiser ID: {}", event.id());
    }
}
