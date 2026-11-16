package ma.youcode.affiliatelink.profile.controller;

import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.common.exception.ResourceNotFoundException;
import ma.youcode.affiliatelink.profile.dto.CreatePublisherRequest;
import ma.youcode.affiliatelink.profile.dto.PublisherResponse;
import ma.youcode.affiliatelink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatelink.profile.service.PublisherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublisherController.class)
@DisplayName("Profile Module: PublisherController Web Slice Tests (@WebMvcTest)")
class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublisherService publisherService;

    @Test
    @DisplayName("POST /api/publishers - Should return 201 Created when payload is valid")
    void shouldReturn201WhenValidPayload() throws Exception {
        // GIVEN
        PublisherResponse response = new PublisherResponse(
                1L,
                "publisher@domain.com",
                ProfileStatus.PENDING,
                LocalDateTime.now(),
                "John",
                "Doe",
                Niche.TECH,
                "Tech blogger",
                "Morocco"
        );

        when(publisherService.createPublisher(any(CreatePublisherRequest.class))).thenReturn(response);

        String jsonPayload = """
                {
                    "email": "publisher@domain.com",
                    "firstName": "John",
                    "lastName": "Doe",
                    "niche": "TECH",
                    "bio": "Tech blogger",
                    "country": "Morocco"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("publisher@domain.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.niche").value("TECH"))
                .andExpect(jsonPath("$.country").value("Morocco"));

        verify(publisherService).createPublisher(any(CreatePublisherRequest.class));
    }

    @Test
    @DisplayName("POST /api/publishers - Should return 400 Bad Request when validation fails")
    void shouldReturn400WhenValidationFails() throws Exception {
        // GIVEN: Missing required fields
        String invalidPayload = """
                {
                    "email": "invalid-email",
                    "firstName": "",
                    "lastName": ""
                }
                """;

        // WHEN & THEN
        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed for one or more fields"))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.niche").exists())
                .andExpect(jsonPath("$.errors.country").exists());
    }

    @Test
    @DisplayName("GET /api/publishers - Should return 200 OK with list of publishers")
    void shouldReturn200WithPublisherList() throws Exception {
        // GIVEN
        List<PublisherResponse> publishers = List.of(
                new PublisherResponse(
                        1L, "p1@test.com", ProfileStatus.ACTIVE, LocalDateTime.now(),
                        "John", "Doe", Niche.TECH, "bio", "Morocco"
                ),
                new PublisherResponse(
                        2L, "p2@test.com", ProfileStatus.PENDING, LocalDateTime.now(),
                        "Jane", "Smith", Niche.SPORT, "sport", "France"
                )
        );

        when(publisherService.getAllPublishers()).thenReturn(publishers);

        // WHEN & THEN
        mockMvc.perform(get("/api/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("p1@test.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("p2@test.com"));

        verify(publisherService).getAllPublishers();
    }

    @Test
    @DisplayName("GET /api/publishers/{id} - Should return 200 OK when publisher exists")
    void shouldReturn200WhenPublisherExists() throws Exception {
        // GIVEN
        PublisherResponse response = new PublisherResponse(
                1L, "p1@test.com", ProfileStatus.ACTIVE, LocalDateTime.now(),
                "John", "Doe", Niche.TECH, "bio", "Morocco"
        );

        when(publisherService.getPublisherById(1L)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(get("/api/publishers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(publisherService).getPublisherById(1L);
    }

    @Test
    @DisplayName("GET /api/publishers/{id} - Should return 404 Not Found when publisher does not exist")
    void shouldReturn404WhenPublisherNotFound() throws Exception {
        // GIVEN
        when(publisherService.getPublisherById(eq(999L)))
                .thenThrow(new ResourceNotFoundException("Publisher not found with id: 999"));

        // WHEN & THEN
        mockMvc.perform(get("/api/publishers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Publisher not found with id: 999"));

        verify(publisherService).getPublisherById(999L);
    }
}
