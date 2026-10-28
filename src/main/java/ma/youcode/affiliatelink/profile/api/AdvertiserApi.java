package ma.youcode.affiliatelink.profile.api;

public interface AdvertiserApi {
    AdvertiserDto getAdvertiserById(Long id);
    boolean isStatusActive(Long id);
}
