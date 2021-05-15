package com.malt.task.test

import com.malt.task.Task
import com.malt.task.TaskId
import com.malt.task.TaskOwnerId
import java.time.OffsetDateTime

data class TaskBuilder(
        var id: TaskId? = null,
        var creationDate: OffsetDateTime? = null,
        var ownerId: TaskOwnerId? = null,
        var summary: String? = null,
        var description: String? = null
) {
    fun build() = Task(
            id = id ?: TaskId.generate(),
            creationDate = creationDate ?: OffsetDateTime.now(),
            ownerId = ownerId ?: TaskOwnerId("some-owner-id"),
            summary = summary ?: "Some task summary",
            description = description ?: "Some task description"
    )
}
