package ma.youcode.affiliatelink.profile.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.dto.CreatePublisherRequest;
import ma.youcode.affiliatelink.profile.dto.PublisherResponse;
import ma.youcode.affiliatelink.profile.entity.Publisher;
import ma.youcode.affiliatelink.profile.mapper.PublisherMapper;
import ma.youcode.affiliatelink.profile.repository.ProfileRepository;
import ma.youcode.affiliatelink.profile.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final ProfileRepository profileRepository;
    private final PublisherMapper publisherMapper;

    @Transactional
    public PublisherResponse createPublisher(CreatePublisherRequest request) {
        if (profileRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email " + request.email() + " is already in use");
        }

        Publisher publisher = publisherMapper.toEntity(request);

        Publisher savedPublisher = publisherRepository.save(publisher);
        return publisherMapper.toResponse(savedPublisher);
    }

    public PublisherResponse getPublisherById(Long id) {
        return publisherRepository.findById(id)
            .map(publisherMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id: " + id));
    }

    public List<PublisherResponse> getAllPublishers() {
        return publisherRepository.findAll()
            .stream()
            .map(publisherMapper::toResponse)
            .toList();
    }
}
