package ma.youcode.affiliatelink.offer.controller;

import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.offer.dto.*;
import ma.youcode.affiliatelink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;
import ma.youcode.affiliatelink.offer.service.OffreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OffreController.class)
@DisplayName("Offer Module: OffreController Web Slice Tests (@WebMvcTest)")
class OffreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OffreService offreService;

    @Test
    @DisplayName("POST /api/offres - Should return 201 Created when payload is valid")
    void shouldReturn201WhenValidPayload() throws Exception {
        // GIVEN
        OffreResponse response = new OffreResponse(
                1L, 1L, "Winter Discount", "Description",
                "https://store.example.com", Niche.TECH,
                LocalDateTime.now(), Set.of("tech", "deals"), List.of()
        );

        when(offreService.create(any(CreateOffreRequest.class))).thenReturn(response);

        String jsonPayload = """
                {
                    "advertiserId": 1,
                    "title": "Winter Discount",
                    "description": "Description",
                    "landingUrl": "https://store.example.com",
                    "niche": "TECH",
                    "tags": ["tech", "deals"]
                }
                """;

        // WHEN & THEN
        mockMvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Winter Discount"))
                .andExpect(jsonPath("$.landingUrl").value("https://store.example.com"))
                .andExpect(jsonPath("$.niche").value("TECH"));

        verify(offreService).create(any(CreateOffreRequest.class));
    }

    @Test
    @DisplayName("POST /api/offres - Should return 400 Bad Request when validation fails")
    void shouldReturn400WhenValidationFails() throws Exception {
        // GIVEN: Missing required fields
        String invalidPayload = """
                {
                    "advertiserId": null,
                    "title": "",
                    "landingUrl": "not-a-valid-url"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(post("/api/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed for one or more fields"))
                .andExpect(jsonPath("$.errors.advertiserId").exists())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.landingUrl").exists())
                .andExpect(jsonPath("$.errors.niche").exists());
    }

    @Test
    @DisplayName("GET /api/offres/{id} - Should return 200 OK when offer exists")
    void shouldReturn200WhenOfferExists() throws Exception {
        // GIVEN
        OffreResponse response = new OffreResponse(
                1L, 1L, "Winter Discount", "Description",
                "https://store.example.com", Niche.TECH,
                LocalDateTime.now(), Set.of("tech"), List.of()
        );

        when(offreService.getOffreById(1L)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(get("/api/offres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Winter Discount"));

        verify(offreService).getOffreById(1L);
    }

    @Test
    @DisplayName("GET /api/offres/{id} - Should return 404 Not Found when offer does not exist")
    void shouldReturn404WhenOfferNotFound() throws Exception {
        // GIVEN
        when(offreService.getOffreById(eq(999L)))
                .thenThrow(new ResourceNotFoundException("Offre not found with id: 999"));

        // WHEN & THEN
        mockMvc.perform(get("/api/offres/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Offre not found with id: 999"));

        verify(offreService).getOffreById(999L);
    }

    @Test
    @DisplayName("GET /api/offres - Should return 200 OK with paginated results")
    void shouldReturn200WithPaginatedResults() throws Exception {
        // GIVEN
        OffreResponse response = new OffreResponse(
                1L, 1L, "Winter Discount", "Description",
                "https://store.example.com", Niche.TECH,
                LocalDateTime.now(), Set.of(), List.of()
        );

        when(offreService.searchOffres(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        // WHEN & THEN
        mockMvc.perform(get("/api/offres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Winter Discount"));

        verify(offreService).searchOffres(any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("POST /api/offres/{id}/campaigns - Should return 201 Created when campaign payload is valid")
    void shouldReturn201WhenCreateCampaignValid() throws Exception {
        // GIVEN
        CampaignResponse campaignResponse = new CampaignResponse(
                1L, "Winter Promo", LocalDate.now(), LocalDate.now().plusMonths(1),
                new BigDecimal("1000.00"), PayoutModel.CPA_FIXED, new BigDecimal("10.00"), CampaignStatus.ACTIVE
        );

        when(offreService.createCampaign(eq(1L), any(CreateCampaignRequest.class))).thenReturn(campaignResponse);

        String jsonPayload = """
                {
                    "name": "Winter Promo",
                    "startDate": "2026-12-01",
                    "endDate": "2026-12-31",
                    "budget": 1000.00,
                    "payoutModel": "CPA_FIXED",
                    "payoutValue": 10.00
                }
                """;

        // WHEN & THEN
        mockMvc.perform(post("/api/offres/1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Winter Promo"))
                .andExpect(jsonPath("$.budget").value(1000.00));

        verify(offreService).createCampaign(eq(1L), any(CreateCampaignRequest.class));
    }
}
