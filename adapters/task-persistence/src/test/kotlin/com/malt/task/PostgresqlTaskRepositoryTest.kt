package com.malt.task

import com.malt.test.postgres.JdbcTemplatePostgresqlTest
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql
import javax.inject.Inject

@JdbcTemplatePostgresqlTest
@Import(PostgresqlTaskRepository::class)
@Sql("/task-schema.sql")
internal class PostgresqlTaskRepositoryTest {

    @Inject
    lateinit var sut: PostgresqlTaskRepository

    @Test
    fun `should perform some test`() {
        println("sut has been injected: $sut")
    }
}