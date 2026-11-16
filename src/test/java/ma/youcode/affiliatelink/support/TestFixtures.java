package ma.youcode.affiliatelink.support;

/**
 * Shared constants and reusable test fixtures used across multiple test classes.
 */
public final class TestFixtures {

    private TestFixtures() {} // Utility class — no instantiation

    // ======================== COMMON TEST CONSTANTS ========================

    public static final String VALID_EMAIL = "test@example.com";
    public static final String INVALID_EMAIL = "not-a-real-email";
    public static final String UNKNOWN_EMAIL = "unknown@example.com";

    public static final Long EXISTING_ID = 1L;
    public static final Long NON_EXISTING_ID = 999L;

    // ======================== PROFILE MODULE ========================

    public static final String ADVERTISER_EMAIL = "advertiser@example.com";
    public static final String PUBLISHER_EMAIL = "publisher@example.com";
    public static final String COMPANY_NAME = "Acme Corp";

    // ======================== OFFER MODULE ========================

    public static final String CAMPAIGN_NAME = "Summer Sale 2025";
}

