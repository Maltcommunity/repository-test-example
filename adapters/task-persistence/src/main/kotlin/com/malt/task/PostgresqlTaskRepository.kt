package com.malt.task

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream
import com.malt.task.TaskColumns as Cols
import com.malt.task.TaskTable as Table

@Transactional(propagation = MANDATORY)
@Repository
internal class PostgresqlTaskRepository(
        private val jdbcTemplate: NamedParameterJdbcTemplate,
) : TaskRepository {

    private val taskMapper = TaskMapper()

    override fun save(task: Task) {
        jdbcTemplate.update("""
            INSERT INTO ${Table.name} (
                ${Cols.id}, ${Cols.creationDate}, ${Cols.ownerId}, ${Cols.summary}, ${Cols.description}   
            ) VALUES (
                :id, :creationDate, :ownerId, :summary, :description
            ) ON CONFLICT (${Cols.id}) DO UPDATE SET
                ${Cols.summary} = :summary,
                ${Cols.description} = :description
        """.trimIndent(), mapOf(
                "id" to task.id.value,
                "creationDate" to task.creationDate,
                "ownerId" to task.ownerId.value,
                "summary" to task.summary,
                "description" to task.description,
        ))
    }

    override fun find(taskId: TaskId): Task? {
        val results = jdbcTemplate.query(
                "SELECT * FROM ${Table.name} WHERE ${Cols.id} = :id LIMIT 2",
                mapOf("id" to taskId.value),
                taskMapper
        )
        check(results.size <= 1) { "More than one task found for $taskId" }
        return results.firstOrNull()
    }

    override fun find(specification: TaskSpecification): Stream<Task> {
        val (sql, params) = specification.toSqlSelection()

        return jdbcTemplate.queryForStream(
                "SELECT * FROM ${Table.name} WHERE $sql",
                params,
                taskMapper
        )
    }

    override fun delete(taskId: TaskId) {
        jdbcTemplate.update(
                "DELETE FROM ${Table.name} WHERE ${Cols.id} = :id",
                mapOf("id" to taskId.value)
        )
    }
}