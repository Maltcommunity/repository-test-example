package com.malt.task.test

import com.malt.task.Task
import com.malt.task.TaskId
import com.malt.task.TaskOwnerId
import java.time.OffsetDateTime

data class TaskBuilder(
        var id: TaskId = TaskId.generate(),
        var creationDate: OffsetDateTime = OffsetDateTime.now(),
        var ownerId: TaskOwnerId = TaskOwnerId("some-owner-id"),
        var summary: String = "Some task summary",
        var description: String? = "Some task description"
) {
    fun build() = Task(
            id = id,
            creationDate = creationDate,
            ownerId = ownerId,
            summary = summary,
            description = description
    )
}
