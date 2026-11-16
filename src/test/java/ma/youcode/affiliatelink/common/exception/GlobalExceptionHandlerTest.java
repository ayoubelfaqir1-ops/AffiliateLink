package ma.youcode.affiliatelink.common.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 */
@DisplayName("Common Module: GlobalExceptionHandler Standalone Tests")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialize explicit Bean Validator for the standalone context
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new TestExceptionController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    // --- Isolated Test Fixtures (Controller & DTO) ---

    record TestPayload(
            @NotBlank(message = "Name is required")
            String name,

            @Email(message = "Email must be valid")
            String email
    ) {}

    @RestController
    static class TestExceptionController {

        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Resource with ID 99 not found");
        }

        @GetMapping("/test/conflict")
        public void throwConflict() {
            throw new BusinessRuleException("Duplicate entity constraint violated");
        }

        @PostMapping("/test/validation")
        public void validatePayload(@Valid @RequestBody TestPayload payload) {
            // No-op if valid
        }
    }

    // --- Test Cases ---

    @Test
    @DisplayName("404 NOT FOUND: Should serialize RFC 7807 ProblemDetail with correct detail message")
    void shouldReturn404ProblemDetail() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Resource with ID 99 not found")));
    }

    @Test
    @DisplayName("409 CONFLICT: Should serialize RFC 7807 ProblemDetail for business rule violations")
    void shouldReturn409ProblemDetail() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.title", is("Conflict")))
                .andExpect(jsonPath("$.detail", is("Duplicate entity constraint violated")));
    }

    @Test
    @DisplayName("400 BAD REQUEST: Should include custom 'errors' map mapping invalid fields to messages")
    void shouldReturn400ProblemDetailWithFieldErrors() throws Exception {
        // GIVEN: Payload violating both @NotBlank and @Email
        String invalidJson = """
                {
                    "name": "",
                    "email": "not-an-email"
                }
                """;

        // WHEN & THEN: Verify 400 Bad Request with both RFC 7807 fields and the custom errors map
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.detail", is("Validation failed for one or more fields")))
                .andExpect(jsonPath("$.errors.name", is("Name is required")))
                .andExpect(jsonPath("$.errors.email", is("Email must be valid")));
    }
}
