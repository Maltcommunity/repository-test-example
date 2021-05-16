package com.malt.task

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneId
import com.malt.task.TaskColumns as Cols

internal class TaskMapper : RowMapper<Task> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Task {
        return Task(
                id = TaskId(rs.getString(Cols.id)),
                creationDate = rs.getTimestamp(Cols.creationDate).toOffsetDateTime(),
                ownerId = TaskOwnerId(rs.getString(Cols.ownerId)),
                summary = rs.getString(Cols.summary),
                description = rs.getString(Cols.description)
        )
    }
}

private fun Timestamp.toOffsetDateTime() =
        OffsetDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())
