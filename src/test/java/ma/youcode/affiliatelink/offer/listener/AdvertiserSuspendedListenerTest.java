package ma.youcode.affiliatelink.offer.listener;

import ma.youcode.affiliatelink.offer.service.CampaignService;
import ma.youcode.affiliatelink.profile.api.AdvertiserSuspended;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Offer Module: AdvertiserSuspendedListener Unit Tests")
class AdvertiserSuspendedListenerTest {

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private AdvertiserSuspendedListener listener;

    @Test
    @DisplayName("Should invoke pauseActiveCampaignsForAdvertiser when event is received")
    void shouldPauseCampaignsWhenAdvertiserSuspendedEventReceived() {
        // GIVEN: A domain event indicating advertiser ID 42 was suspended
        Long suspendedAdvertiserId = 42L;
        AdvertiserSuspended event = new AdvertiserSuspended(suspendedAdvertiserId, "advertiser@domain.com");

        // WHEN: Listener handles the event
        listener.onAdvertiserSuspended(event);

        // THEN: The campaign service is called to pause active campaigns
        verify(campaignService).pauseActiveCampaignsForAdvertiser(suspendedAdvertiserId);
    }
}

