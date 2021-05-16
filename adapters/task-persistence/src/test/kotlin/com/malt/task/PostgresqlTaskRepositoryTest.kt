package com.malt.task

import com.malt.test.postgres.JdbcTemplatePostgresqlTest
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql
import javax.inject.Inject

@JdbcTemplatePostgresqlTest
@Import(PostgresqlTaskRepository::class)
@Sql("/task-schema.sql")
internal class PostgresqlTaskRepositoryTest : TaskRepositoryContract() {

    @Inject
    lateinit var sut: PostgresqlTaskRepository

    override fun buildRepositoryUnderTest() = sut
}