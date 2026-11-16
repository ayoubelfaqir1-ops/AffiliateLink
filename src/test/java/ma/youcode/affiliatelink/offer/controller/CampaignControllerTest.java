package ma.youcode.affiliatelink.offer.controller;

import ma.youcode.affiliatelink.common.exception.BusinessRuleException;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.offer.dto.CampaignResponse;
import ma.youcode.affiliatelink.offer.dto.UpdateCampaignStatusRequest;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;
import ma.youcode.affiliatelink.offer.service.CampaignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CampaignController.class)
@DisplayName("Offer Module: CampaignController Web Slice Tests (@WebMvcTest)")
class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CampaignService campaignService;

    @Test
    @DisplayName("GET /api/campaigns - Should return 200 OK with list of campaigns")
    void shouldReturn200WithCampaignList() throws Exception {
        // GIVEN
        List<CampaignResponse> campaigns = List.of(
                new CampaignResponse(
                        1L, "Winter Promo", LocalDate.now(), LocalDate.now().plusMonths(1),
                        new BigDecimal("1000.00"), PayoutModel.CPA_FIXED, new BigDecimal("10.00"), CampaignStatus.ACTIVE
                )
        );

        when(campaignService.getCampaigns(null)).thenReturn(campaigns);

        // WHEN & THEN
        mockMvc.perform(get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Winter Promo"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(campaignService).getCampaigns(null);
    }

    @Test
    @DisplayName("GET /api/campaigns?status=ACTIVE - Should pass filter parameter to service")
    void shouldFilterCampaignsByStatus() throws Exception {
        // GIVEN
        when(campaignService.getCampaigns(CampaignStatus.ACTIVE)).thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/campaigns").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(campaignService).getCampaigns(CampaignStatus.ACTIVE);
    }

    @Test
    @DisplayName("PATCH /api/campaigns/{id}/status - Should return 204 No Content when status update is valid")
    void shouldReturn204WhenStatusUpdateValid() throws Exception {
        // GIVEN
        String jsonPayload = """
                {
                    "status": "PAUSED"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(patch("/api/campaigns/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNoContent());

        verify(campaignService).updateStatus(eq(1L), any(UpdateCampaignStatusRequest.class));
    }

    @Test
    @DisplayName("PATCH /api/campaigns/{id}/status - Should return 409 Conflict when transition is invalid")
    void shouldReturn409WhenTransitionInvalid() throws Exception {
        // GIVEN: Service throws BusinessRuleException
        doThrow(new BusinessRuleException("Cannot transition campaign from DRAFT to PAUSED"))
                .when(campaignService).updateStatus(eq(1L), any(UpdateCampaignStatusRequest.class));

        String jsonPayload = """
                {
                    "status": "PAUSED"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(patch("/api/campaigns/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Cannot transition campaign from DRAFT to PAUSED"));
    }

    @Test
    @DisplayName("PATCH /api/campaigns/{id}/status - Should return 404 Not Found when campaign not found")
    void shouldReturn404WhenCampaignNotFound() throws Exception {
        // GIVEN
        doThrow(new ResourceNotFoundException("Campaign not found with id: 999"))
                .when(campaignService).updateStatus(eq(999L), any(UpdateCampaignStatusRequest.class));

        String jsonPayload = """
                {
                    "status": "ACTIVE"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(patch("/api/campaigns/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Campaign not found with id: 999"));
    }

    @Test
    @DisplayName("PATCH /api/campaigns/{id}/status - Should return 400 Bad Request when status is null")
    void shouldReturn400WhenStatusNull() throws Exception {
        // GIVEN
        String jsonPayload = "{}";

        // WHEN & THEN
        mockMvc.perform(patch("/api/campaigns/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed for one or more fields"))
                .andExpect(jsonPath("$.errors.status").value("Status is required"));
    }
}
