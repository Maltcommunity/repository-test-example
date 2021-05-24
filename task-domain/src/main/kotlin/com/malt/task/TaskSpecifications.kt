package com.malt.task

sealed interface TaskSpecification {

    infix fun isSatisfiedBy(task: Task): Boolean

    infix fun and(other: TaskSpecification) = AndTaskSpecification(this, other)

    operator fun not() = NotTaskSpecification(this)

    infix fun or(other: TaskSpecification) = OrTaskSpecification(this, other)
}

fun allOf(vararg specs: TaskSpecification): TaskSpecification {
    require(specs.isNotEmpty()) { "At least one specification must be provided" }
    return specs.reduce { acc, spec -> acc and spec }
}

fun anyOf(vararg specs: TaskSpecification): TaskSpecification {
    require(specs.isNotEmpty()) { "At least one specification must be provided" }
    return specs.reduce { acc, spec -> acc or spec }
}

fun noneOf(vararg specs: TaskSpecification): TaskSpecification {
    require(specs.isNotEmpty()) { "At least one specification must be provided" }
    return specs.fold<TaskSpecification, TaskSpecification>(!specs[0]) { acc, spec -> acc and !spec }
}

data class AndTaskSpecification(
        val left: TaskSpecification,
        val right: TaskSpecification
) : TaskSpecification {

    override fun isSatisfiedBy(task: Task) = left.isSatisfiedBy(task) && right.isSatisfiedBy(task)
}

data class OrTaskSpecification(
        val left: TaskSpecification,
        val right: TaskSpecification
) : TaskSpecification {

    override fun isSatisfiedBy(task: Task) = left.isSatisfiedBy(task) || right.isSatisfiedBy(task)
}

data class NotTaskSpecification(val spec: TaskSpecification) : TaskSpecification {

    override fun isSatisfiedBy(task: Task) = !spec.isSatisfiedBy(task)
}

data class TaskIdIs(val taskId: TaskId) : TaskSpecification {

    override fun isSatisfiedBy(task: Task) = task.id == taskId
}

data class TaskOwnerIdIs(val ownerId: TaskOwnerId) : TaskSpecification {

    override fun isSatisfiedBy(task: Task) = task.ownerId == ownerId
}

// other possible specifications:
// - TaskCreatedAfter(val date: OffsetDateTime)
// - TaskOverdue(val now: OffsetDateTime)  (if we give Task a dueDate)
// - ...