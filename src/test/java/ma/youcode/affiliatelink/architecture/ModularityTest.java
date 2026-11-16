package ma.youcode.affiliatelink.architecture;

import ma.youcode.affiliatelink.AffiliateLinkApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Architecture-level test that verifies the modular structure of the application.
 * Ensures all Spring Modulith module boundaries and dependencies are respected.
 */
public class ModularityTest {

    static ApplicationModules modules = ApplicationModules.of(AffiliateLinkApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.forEach(System.out::println);
        modules.verify();
    }
}

