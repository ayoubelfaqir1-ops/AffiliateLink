package ma.youcode.affiliatelink.offer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.UpdateCampaignStatusRequest;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.service.CampaignService;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getCampaigns(@RequestParam(required = false) CampaignStatus status) {
        return ResponseEntity.ok(campaignService.getCampaigns(status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CampaignResponse> updateStatus(@PathVariable Long id ,@Valid @RequestBody UpdateCampaignStatusRequest request) {
        campaignService.updateStatus(id ,request);
        return ResponseEntity.noContent().build();
    }
}
