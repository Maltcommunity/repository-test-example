package com.malt.task.test

import com.malt.task.Task
import com.malt.task.TaskId
import com.malt.task.TaskRepository
import com.malt.task.TaskSpecification
import java.util.stream.Stream

class InMemoryTaskRepository : TaskRepository {

    private val tasks = mutableListOf<Task>()

    override fun save(task: Task) {
        tasks.removeIf { it.id == task.id }
        tasks.add(task)
    }

    override fun find(taskId: TaskId) = tasks.firstOrNull { it.id == taskId }

    override fun find(specification: TaskSpecification): Stream<Task> =
            tasks.stream().filter { specification.isSatisfiedBy(it) }
}