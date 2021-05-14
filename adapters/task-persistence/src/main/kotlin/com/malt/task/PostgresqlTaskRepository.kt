package com.malt.task

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class PostgresqlTaskRepository(
        private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    // empty shell for now

    fun doSomethingWith(vararg tasks: Task) {
        TODO()
    }
}