package ma.youcode.affiliatelink.profile.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.dto.UpdateProfileStatusRequest;
import ma.youcode.affiliatelink.profile.service.ProfileAdminService;

@WebMvcTest(ProfileController.class)
@DisplayName("Profile Module: ProfileController Web Slice Tests (@WebMvcTest)")
public class ProfileControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileAdminService profileAdminService;

    @Test
    @DisplayName("PATCH /api/admin/profiles/{id}/status - Should return 204 No Content when transition is valid")
    void shouldReturn204WhenValidTransition() throws Exception {
        // GIVEN: Valid payload
        String jsonPayload = """
                {
                    "status": "ACTIVE"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(patch("/api/admin/profiles/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNoContent());

        verify(profileAdminService).updateProfileStatus(eq(1L), any(UpdateProfileStatusRequest.class));
    }

    @Test
    @DisplayName("PATCH /api/admin/profiles/{id}/status - Should return 409 Conflict when transition is illegal")
    void shouldReturn409WhenIllegalTransition() throws Exception {
        // GIVEN: Service throws BusinessRuleException
        doThrow(new BusinessRuleException("Transition PENDING -> SUSPENDED is not allowed"))
                .when(profileAdminService).updateProfileStatus(eq(1L), any(UpdateProfileStatusRequest.class));

        String jsonPayload = """
                {
                    "status": "SUSPENDED"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(patch("/api/admin/profiles/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Transition PENDING -> SUSPENDED is not allowed"));
    }

    @Test
    @DisplayName("PATCH /api/admin/profiles/{id}/status - Should return 404 Not Found when profile does not exist")
    void shouldReturn404WhenProfileNotFound() throws Exception {
        // GIVEN: Service throws ResourceNotFoundException
        doThrow(new ResourceNotFoundException("Profile not found with id: 999"))
                .when(profileAdminService).updateProfileStatus(eq(999L), any(UpdateProfileStatusRequest.class));

        String jsonPayload = """
                {
                    "status": "ACTIVE"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(patch("/api/admin/profiles/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Profile not found with id: 999"));
    }

    @Test
    @DisplayName("PATCH /api/admin/profiles/{id}/status - Should return 400 Bad Request when status is null")
    void shouldReturn400WhenStatusNull() throws Exception {
        // GIVEN: Missing status field
        String jsonPayload = "{}";

        // WHEN & THEN
        mockMvc.perform(patch("/api/admin/profiles/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed for one or more fields"))
                .andExpect(jsonPath("$.errors.status").value("Target status is required"));
    }
}
