package com.malt.task

object TaskTable {
    const val name = "task.tasks"
    val columns = TaskColumns
}

object TaskColumns {
    const val id = "id"
    const val creationDate = "creation_date"
    const val ownerId = "owner_id"
    const val summary = "summary"
    const val description = "description"
}
