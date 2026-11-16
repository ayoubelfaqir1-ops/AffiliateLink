package ma.youcode.affiliatelink.profile.controller;

import ma.youcode.affiliatelink.profile.dto.AdvertiserResponse;
import ma.youcode.affiliatelink.profile.dto.CreateAdvertiserRequest;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.service.AdvertiserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web MVC slice tests for {@link AdvertiserController}.
 */
@WebMvcTest(AdvertiserController.class)
@DisplayName("Profile Module: AdvertiserController Web Slice Tests (@WebMvcTest)")
class AdvertiserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvertiserService advertiserService;

    @Test
    @DisplayName("POST /api/advertisers - Should return 201 Created when payload is valid")
    void shouldReturn201WhenValidPayload() throws Exception {
        // GIVEN: Service returns a created response
        AdvertiserResponse response = new AdvertiserResponse(
                1L,
                "corp@adtech.com",
                ProfileStatus.PENDING,
                LocalDateTime.now(),
                "AdTech Global",
                "Marketing",
                "https://adtech.com"
        );
        when(advertiserService.createAdvertiser(any(CreateAdvertiserRequest.class))).thenReturn(response);

        String jsonPayload = """
                {
                    "email": "corp@adtech.com",
                    "companyName": "AdTech Global",
                    "sector": "Marketing",
                    "website": "https://adtech.com"
                }
                """;

        // WHEN & THEN: Simulate HTTP POST and verify 201 Created
        mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("corp@adtech.com"))
                .andExpect(jsonPath("$.companyName").value("AdTech Global"));

        verify(advertiserService).createAdvertiser(any(CreateAdvertiserRequest.class));
    }

    @Test
    @DisplayName("POST /api/advertisers - Should return 400 Bad Request when validation fails")
    void shouldReturn400WhenInvalidPayload() throws Exception {
        // GIVEN: Invalid payload with empty companyName and invalid email format
        String invalidPayload = """
                {
                    "email": "not-an-email",
                    "companyName": "",
                    "sector": "Marketing",
                    "website": "https://adtech.com"
                }
                """;

        // WHEN & THEN: Verify Bean Validation triggers HTTP 400 without invoking the service
        mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/advertisers/{id} - Should return 200 OK with advertiser details")
    void shouldReturn200WhenAdvertiserFound() throws Exception {
        // GIVEN
        AdvertiserResponse response = new AdvertiserResponse(
                10L,
                "brand@domain.com",
                ProfileStatus.ACTIVE,
                LocalDateTime.now(),
                "Super Brand",
                "Retail",
                "https://brand.com"
        );
        when(advertiserService.getAdvertiserById(10L)).thenReturn(response);

        // WHEN & THEN: Simulate HTTP GET /api/advertisers/10
        mockMvc.perform(get("/api/advertisers/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.companyName").value("Super Brand"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(advertiserService).getAdvertiserById(10L);
    }
}

