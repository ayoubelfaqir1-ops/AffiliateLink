package ma.youcode.affiliatelink.profile.service;

import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.dto.CreatePublisherRequest;
import ma.youcode.affiliatelink.profile.dto.PublisherResponse;
import ma.youcode.affiliatelink.profile.entity.Publisher;
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

    @Transactional
    public PublisherResponse createPublisher(CreatePublisherRequest request) {
        if (profileRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email " + request.email() + " is already in use");
        }

        Publisher publisher = new Publisher(
            request.email(),
            request.firstName(),
            request.lastName(),
            request.niche(),
            request.bio(),
            request.country()
        );

        Publisher savedPublisher = publisherRepository.save(publisher);
        return PublisherResponse.from(savedPublisher);
    }

    public PublisherResponse getPublisherById(Long id) {
        return publisherRepository.findById(id)
            .map(PublisherResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id: " + id));
    }

    public List<PublisherResponse> getAllPublishers() {
        return publisherRepository.findAll()
            .stream()
            .map(PublisherResponse::from)
            .toList();
    }
}
