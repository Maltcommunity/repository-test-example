package com.malt.task

import java.util.stream.Stream

interface TaskRepository {

    fun save(task: Task)

    fun find(taskId: TaskId): Task?

    // In case one wonders why we don't use a Kotlin Sequence here: they aren't AutoCloseable and
    // therefore aren't a good abstraction to hide DB cursors. While it may be debatable whether
    // Stream is a good abstraction, it's AutoCloseable and is already used by many tools for that
    // purpose.
    // See https://youtrack.jetbrains.com/issue/KT-34719 for the perspective of the Kotlin team.
    fun find(specification: TaskSpecification): Stream<Task>

    fun delete(taskId: TaskId)
}