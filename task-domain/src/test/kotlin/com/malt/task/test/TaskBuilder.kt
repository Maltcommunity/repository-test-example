package com.malt.task.test

import com.malt.task.Task
import com.malt.task.TaskId
import com.malt.task.TaskOwnerId
import java.time.OffsetDateTime
import java.util.*

/**
 * Helps building a Task:
 * - with only those attributes that are useful to a given test, the other attributes receiving
 *   default values
 * - by providing those attributes in several steps if needed
 * - and using addition ad-hoc logic if needed (see [withRandomDescription])
 */
data class TaskBuilder(
        var id: TaskId = TaskId.generate(),
        var creationDate: OffsetDateTime = OffsetDateTime.now(),
        var ownerId: TaskOwnerId = TaskOwnerId("some-owner-id"),
        var summary: String = "Some task summary",
        var description: String? = "Some task description"
) {

    fun withRandomDescription(): TaskBuilder {
        description = UUID.randomUUID().toString()
        return this
    }

    fun build() = Task(
            id = id,
            creationDate = creationDate,
            ownerId = ownerId,
            summary = summary,
            description = description
    )
}
