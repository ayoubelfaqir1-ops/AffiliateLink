package ma.youcode.affiliatelink.profile.api;

public interface PublisherApi {
    PublisherDto getPublisherById(Long id);
    boolean isStatusActive(Long id);
}
