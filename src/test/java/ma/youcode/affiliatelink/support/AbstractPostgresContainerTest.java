package ma.youcode.affiliatelink.support;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase; 
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base abstract class for integration tests running against a shared PostgreSQL container.
 */
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractPostgresContainerTest {

    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        postgres.start();
    }
}
