package ma.youcode.affiliatelink.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.youcode.affiliatelink.profile.dto.UpdateProfileStatusRequest;
import ma.youcode.affiliatelink.profile.service.ProfileAdminService;

@RestController 
@RequestMapping("/api/admin/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileAdminService service;

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,@Valid @RequestBody UpdateProfileStatusRequest request) {
        service.updateProfileStatus(id, request);
        return ResponseEntity.noContent().build();
    }
}
