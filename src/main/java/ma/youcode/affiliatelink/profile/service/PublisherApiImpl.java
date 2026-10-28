package ma.youcode.affiliatelink.profile.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.api.PublisherApi;
import ma.youcode.affiliatelink.profile.api.PublisherDto;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.mapper.PublisherMapper;
import ma.youcode.affiliatelink.profile.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherApiImpl implements PublisherApi {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    @Override
    public PublisherDto getPublisherById(Long id) {
        return publisherRepository.findById(id)
            .map(publisherMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id: " + id));
    }

    @Override
    public boolean isStatusActive(Long id) {
        return publisherRepository.findById(id)
            .map(publisher -> publisher.getStatus() == ProfileStatus.ACTIVE)
            .orElse(false);
    }
}
