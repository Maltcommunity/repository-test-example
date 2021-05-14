package com.malt.test.postgres;

import com.malt.test.testcontainers.TestContainersUserProperties;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.util.TestPropertyValues.Type;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.annotation.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

/**
 * A test using a real (containerized) PostgreSQL instance and configuring a Spring datasource to
 * use it.
 *
 * <p>Example:</p>
 * <pre>
 * &#064;PostgresqlTest
 * &#064;Import(MyRepository.class)
 * &#064;@Sql("/some-schema-or-data.sql")  // optional
 * class MyRepositoryTest {
 *
 *     &#064;Inject
 *     MyRepository repositoryUnderTest;
 *
 *     &#064;Test
 *     void should_do_something() {
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @see JdbcTemplatePostgresqlTest &#064;JdbcTemplatePostgresqlTest for tests using Spring's JdbcTemplate
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@ExtendWith(PostgresqlTest.Extension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        initializers = PostgresqlTest.Initializer.class,
        // useless but Spring won't accept a @ContextConfiguration without classes or locations
        classes = PostgresqlTest.Initializer.NullComponent.class
)
@DirtiesContext
@Tag("integration")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface PostgresqlTest {

    class Extension implements BeforeAllCallback, AfterAllCallback {

        private static final String MAIN_TEST_DB_NAME = "TEST_DB";
        private static final String DB_NAME_KEY = "DATABASE_NAME";
        private static final String DB_NAME_PREFIX = "task-";
        private static final String STORE_KEY = "pg_ctx";
        private static final Namespace NAMESPACE = Namespace.create(Extension.class);

        private static final ThreadLocal<PostgreSQLContainer<?>> containerForCurrentThread = new ThreadLocal<>();
        private static final ThreadLocal<String> dbNameForCurrentThread = new ThreadLocal<>();

        @Override
        public void beforeAll(ExtensionContext extensionContext) throws Exception {
            PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:11.2")
                    .withDatabaseName(MAIN_TEST_DB_NAME)
                    .withUsername("amin")
                    .withPassword("admin")
                    .withStartupTimeout(Duration.ofSeconds(600))
                    .withReuse(true);

            getStore(extensionContext).put(STORE_KEY, new PostgresqlExtensionContext(
                    extensionContext.getRequiredTestClass(),
                    container
            ));

            if (!container.isRunning()) {
                TestContainersUserProperties.attemptToDisableTestcontainersStartupsChecks();
                container.start();
            }
            beforeAllWithRunningContainer(extensionContext, container);
        }

        private void beforeAllWithRunningContainer(ExtensionContext context, PostgreSQLContainer<?> container) throws Exception {
            var dbName = DB_NAME_PREFIX + UUID.randomUUID();
            try (Connection connection = container.createConnection("")) {
                connection.createStatement().execute("CREATE DATABASE \"" + dbName + "\";");
            }
            context.getStore(NAMESPACE).put(DB_NAME_KEY, dbName);
            dbNameForCurrentThread.set(dbName);
            containerForCurrentThread.set(container);
        }

        @Override
        public void afterAll(ExtensionContext extensionContext) throws Exception {
            var store = getStore(extensionContext);
            var ctx = store.get(STORE_KEY, PostgresqlExtensionContext.class);
            if (!ctx.getEntryPoint().equals(extensionContext.getRequiredTestClass())) {
                return;
            }

            var container = ctx.getContainer();

            if (container.isRunning()) {
                afterAllWithRunningContainer(extensionContext, container);
                if (!container.isShouldBeReused()) {
                    container.stop();
                }
            }
        }

        private void afterAllWithRunningContainer(ExtensionContext context, PostgreSQLContainer<?> container) throws Exception {
            var dbName = context.getStore(NAMESPACE).get(DB_NAME_KEY, String.class);
            if (dbName == null) {
                return;
            }

            try (Connection connection = container.createConnection("")) {
                try {
                    dropDatabase(dbName, connection);
                } catch (Exception e) {
                    // poor man's retry, in case database is still accessed: it lets some time for the session to be closed
                    Thread.sleep(50);
                    dropDatabase(dbName, connection);
                }
            }
            context.getStore(NAMESPACE).remove(DB_NAME_KEY);
            dbNameForCurrentThread.remove();
            containerForCurrentThread.remove();
        }

        @SuppressWarnings("SqlResolve")
        private void dropDatabase(String dbName, Connection connection) throws SQLException {
            connection.createStatement().execute(
                    // prevent new connections to the database
                    "REVOKE CONNECT ON DATABASE \"" + dbName + "\" FROM public;" +
                            // kill existing connections to the database
                            "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '" + dbName + "';" +
                            // now we can drop the database
                            "DROP DATABASE \"" + dbName + "\";"
            );
        }

        private Store getStore(ExtensionContext context) {
            return context.getStore(NAMESPACE);
        }

    }

    class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        static class NullComponent {
        }

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            var dbName = Extension.dbNameForCurrentThread.get();
            var container = Extension.containerForCurrentThread.get();
            var jdbcUrl = container.getJdbcUrl().replaceFirst(Extension.MAIN_TEST_DB_NAME, dbName);

            TestPropertyValues.of(
                    "spring.datasource.url=" + jdbcUrl,
                    "spring.datasource.username=" + container.getUsername(),
                    "spring.datasource.password=" + container.getPassword(),
                    "spring.datasource.initialization-mode=always"
            )
                    .applyTo(configurableApplicationContext.getEnvironment(), Type.MAP, "testcontainers");
        }
    }
}
