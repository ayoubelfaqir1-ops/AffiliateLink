package ma.youcode.affiliatelink.offer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatelink.offer.dto.CreateOffreRequest;
import ma.youcode.affiliatelink.offer.dto.OffreResponse;
import ma.youcode.affiliatelink.offer.dto.UpdateOffreRequest;
import ma.youcode.affiliatelink.offer.service.OffreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
public class OffreController {

    private final OffreService offreService;

    @PostMapping
    public ResponseEntity<OffreResponse> create(@Valid @RequestBody CreateOffreRequest request) {
        OffreResponse offreResponse = offreService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(offreResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffreResponse> getOffreById(@PathVariable Long id) {
        return ResponseEntity.ok(offreService.getOffreById(id));
    }

    @PutMapping ("/{id}")
    public ResponseEntity<OffreResponse> update(@PathVariable Long id,@Valid @RequestBody UpdateOffreRequest request) {
        OffreResponse offreResponse = offreService.updateOffre(id ,request);
        return ResponseEntity.ok(offreResponse);
    }

    @PostMapping("/{id}/campaigns")
    public ResponseEntity<CampaignResponse> createCampaign(@PathVariable Long id ,@Valid @RequestBody CreateCampaignRequest request) {
        CampaignResponse campaignResponse = offreService.createCampaign(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignResponse);
    }
}
