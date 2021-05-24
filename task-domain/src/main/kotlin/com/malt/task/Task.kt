package com.malt.task

import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

// Some design decisions:
//
// 1. Don't use a data class:
//    - The generated `copy()` method of data classes allows bypassing validations that may be
//      present in constructors & setters, which is isn't suitable for a domain object that must
//      protect its invariants.
//    - The generated `equals()` method of data classes is more suitable to value objects. An
//      entity equality should only be derived from its ID. While we could override the generated
//      `equals()` method, we find it preferable to keep a default `equals()` method with a
//      "is same" meaning and to explicitly compare IDs when needed.
//
// 2. Don't let the database generate IDs:
//    - That way the domain really is independent, and any entity created in the code has a clear
//      identity.
//    - It's also beneficial to a CQRS approach, though we don't really care in that sample
//      codebase.
//
//
// 3. Otherwise, keep the example simple and take appropriate shortcuts to that end:
//    - While the previous points are strong preferences that we chose to expose here, we're not
//      aiming for a state-of-the-art example on all points (the context is key anyway).
//    - Shortcut 1: while we gave `Task` an `ownerId`, the concept of a `TaskOwner` in itself isn't
//      developed in this codebase. The `ownerId` is enough to introduce real-ish use cases.
//    - Shortcut 2: it's likely that a real system would benefit from having some kind of simple
//      reference or sequence for a given user's tasks, so that they are easier to work with. This
//      would bring additional - unnecessary - complexity to this example, so we'll stick to the
//      quite technical `TaskId`.
class Task(
        val id: TaskId = TaskId.generate(),
        val creationDate: OffsetDateTime = OffsetDateTime.now(),
        val ownerId: TaskOwnerId,
        val summary: String,
        val description: String?

        // we may easily imagine adding:
        // - a `status`: TO_DO or DONE
        // - a `dueDate` and logic related to overdue tasks
) {

    constructor(
            id: TaskId = TaskId.generate(),
            clock: Clock,
            ownerId: TaskOwnerId,
            summary: String,
            description: String?
    ) : this(
            id = id,
            creationDate = OffsetDateTime.now(clock),
            ownerId = ownerId,
            summary = summary,
            description = description
    )

    init {
        validateSummary(summary)
    }

    private fun validateSummary(summary: String) {
        require(isAValidTaskSummary(summary)) { "Task summary can't be blank" }
    }

    fun withSummary(newSummary: String) = copy(summary = newSummary)

    fun withDescription(newDescription: String?) = copy(description = newDescription)

    /**
     * A copy method similar to that of a data class, to help other methods return a slightly
     * modified version of the current object
     */
    private fun copy(
            id: TaskId? = null,
            creationDate: OffsetDateTime? = null,
            ownerId: TaskOwnerId? = null,
            summary: String? = null,
            description: String? = null
    ) = Task(
            id = id ?: this.id,
            creationDate = creationDate ?: this.creationDate,
            ownerId = ownerId ?: this.ownerId,
            summary = summary ?: this.summary,
            description = description ?: this.description
    )
}

data class TaskId(val value: String) {
    companion object {
        fun generate() = TaskId(UUID.randomUUID().toString())
    }
}

fun isAValidTaskSummary(summary: String) = summary.isNotBlank()
