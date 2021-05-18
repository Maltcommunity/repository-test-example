package com.malt.task

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.stream.Stream

@Service
class CurrentUserTaskService(
        private val clock: Clock,
        private val taskRepository: TaskRepository
) {

    @Transactional
    fun addTaskForUser(summary: String, description: String?): Task {
        val newTask = Task(
                clock = clock,
                ownerId = CurrentTaskOwnerIdHolder.currentTaskOwnerId,
                summary = summary,
                description = description
        )
        taskRepository.save(newTask)

        return newTask
    }

    @Transactional(readOnly = true)
    fun findAllUserTasks(): Stream<Task> {
        return taskRepository.find(TaskOwnerIdIs(CurrentTaskOwnerIdHolder.currentTaskOwnerId))
    }
}