package ma.youcode.affiliatelink.common;

/**
 * Lightweight DTO for scalar projections of an Offer.
 * Located in the 'common' module; does NOT depend on any internal module entities
 * to maintain strict Spring Modulith boundaries.
 */
public record OffreSummaryDto(
        Long id,
        String title,
        String description
) {
}
