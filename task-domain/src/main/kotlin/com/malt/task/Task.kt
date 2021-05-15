package com.malt.task

import java.time.OffsetDateTime
import java.util.*

data class Task(
        val id: TaskId = TaskId.generate(),
        val creationDate: OffsetDateTime = OffsetDateTime.now(),
        val ownerId: TaskOwnerId,
        val summary: String,
        val description: String
)

data class TaskId(val value: String) {
    companion object {
        fun generate() = TaskId(UUID.randomUUID().toString())
    }
}
