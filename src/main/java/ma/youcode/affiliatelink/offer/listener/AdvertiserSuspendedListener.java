package ma.youcode.affiliatelink.offer.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.youcode.affiliatelink.offer.service.CampaignService;
import ma.youcode.affiliatelink.profile.api.AdvertiserSuspended;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdvertiserSuspendedListener {

    private final CampaignService campaignService;

    @ApplicationModuleListener
    public void onAdvertiserSuspended(AdvertiserSuspended event) {
        log.info("Received AdvertiserSuspended event for advertiser ID: {}. Processing campaign pauses.", event.advertiserId());
        campaignService.pauseActiveCampaignsForAdvertiser(event.advertiserId());
        log.info("Finished processing AdvertiserSuspended event for advertiser ID: {}", event.advertiserId());
    }
}
