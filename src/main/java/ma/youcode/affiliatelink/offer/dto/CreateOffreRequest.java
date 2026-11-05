package ma.youcode.affiliatelink.offer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ma.youcode.affiliatelink.common.enums.Niche;

import java.util.Set;


public record CreateOffreRequest(
    @NotNull(message = "Advertiser ID is required")
    Long advertiserId,

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    String title,

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    String description,

    @NotBlank(message = "Landing URL is required")
    @Size(max = 255, message = "Landing URL must not exceed 255 characters")
    @Pattern(
        regexp = "^(https?://).+",
        message = "Landing URL must be a valid HTTP or HTTPS address"
    )
    String landingUrl,

    @NotNull(message = "Niche is required")
    Niche niche,

    Set<@NotBlank(message = "Tag name cannot be blank") @Size(max = 50, message = "Tag name must not exceed 50 characters") String> tags
) {
    public CreateOffreRequest {
        tags = tags == null ? Set.of() : tags;
    }
}
