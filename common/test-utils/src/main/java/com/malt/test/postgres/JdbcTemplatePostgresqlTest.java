package com.malt.test.postgres;

import com.malt.test.postgres.PostgresqlTest.Initializer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

/**
 * A test using a real (containerized) PostgreSQL instance and configuring a Spring datasource and
 * JdbcTemplate to use it.
 *
 * <p>Example:</p>
 * <pre>
 * &#064;JdbcTemplatePostgresqlTest
 * &#064;Import(MyJdbcTemplateRepository.class)
 * &#064;@Sql("/some-schema-or-data.sql")  // optional
 * class MyJdbcTemplateRepositoryTest {
 *
 *     &#064;Inject
 *     MyJdbcTemplateRepository repositoryUnderTest;
 *
 *     &#064;Test
 *     void should_do_something() {
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @see PostgresqlTest
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@PostgresqlTest
@JdbcTest
// overrides what's defined in @JdbcTest above
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
        initializers = Initializer.class,
        // useless but Spring won't accept a @ContextConfiguration without classes or locations
        classes = Initializer.NullComponent.class
)
public @interface JdbcTemplatePostgresqlTest {
}
