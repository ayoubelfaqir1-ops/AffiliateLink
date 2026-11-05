package ma.youcode.affiliatelink.offer.api;

public interface OfferApi {
    CampaignDto getCampaignById(Long id);
    boolean isCampaignActive(Long id);
}
