package ma.youcode.affiliatelink.support;

import ma.youcode.affiliatelink.common.enums.Niche;
import ma.youcode.affiliatelink.offer.entity.Campaign;
import ma.youcode.affiliatelink.offer.entity.enums.PayoutModel;
import ma.youcode.affiliatelink.profile.entity.Advertiser;
import ma.youcode.affiliatelink.profile.entity.Publisher;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Test data factory providing standard entities and fixtures for test suites.
 */
public final class TestDataFactory {

    private TestDataFactory() {} // Utility class — no instantiation

    // ======================== PROFILE MODULE ========================

    public static Advertiser anAdvertiser() {
        return new Advertiser(
                "advertiser@example.com",
                "Acme Corp",
                "Technology",
                "https://acme.example.com"
        );
    }

    public static Advertiser anAdvertiserWithEmail(String email) {
        return new Advertiser(
                email,
                "Test Company",
                "Technology",
                "https://test.example.com"
        );
    }

    public static Publisher aPublisher() {
        return new Publisher(
                "publisher@example.com",
                "Sara",
                "Benali",
                Niche.TECH,
                "Tech blogger and content creator",
                "Morocco"
        );
    }

    public static Publisher aPublisherWithEmail(String email) {
        return new Publisher(
                email,
                "Test",
                "Publisher",
                Niche.LIFESTYLE,
                "Test bio",
                "Morocco"
        );
    }

    // ======================== OFFER MODULE ========================

    public static Campaign aCampaign() {
        return new Campaign(
                "Summer Sale 2025",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(30),
                new BigDecimal("5000.00"),
                PayoutModel.CPC,
                new BigDecimal("1.50")
        );
    }

    public static Campaign aCampaignWithName(String name) {
        return new Campaign(
                name,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(30),
                new BigDecimal("3000.00"),
                PayoutModel.CPL,
                new BigDecimal("5.00")
        );
    }
}

