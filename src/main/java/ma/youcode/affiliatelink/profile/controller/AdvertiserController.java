package ma.youcode.affiliatelink.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.profile.dto.AdvertiserResponse;
import ma.youcode.affiliatelink.profile.dto.CreateAdvertiserRequest;
import ma.youcode.affiliatelink.profile.service.AdvertiserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisers")
@RequiredArgsConstructor
public class AdvertiserController {

    private final AdvertiserService advertiserService ;

    @PostMapping
    public ResponseEntity<AdvertiserResponse> createPublisher(@Valid @RequestBody CreateAdvertiserRequest request) {
        AdvertiserResponse created = advertiserService.createAdvertiser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<AdvertiserResponse>> getAllPublishers() {
        return ResponseEntity.ok(advertiserService.getAllAdvertisers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertiserResponse> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(advertiserService.getAdvertiserById(id));
    }
}
