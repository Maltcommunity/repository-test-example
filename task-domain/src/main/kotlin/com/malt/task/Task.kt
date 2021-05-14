package com.malt.task

data class Task(
        val id: TaskId = TaskId.generate(),
        val creationDate: OffsetDateTime = OffsetDateTime.now(),
        val summary: String,
        val description: String
)

data class TaskId(val value: String)
