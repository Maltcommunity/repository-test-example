package com.malt.task

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import kotlin.streams.toList

@Service
class CurrentUserTaskService(
        private val clock: Clock,
        private val taskMergeService: TaskMergeService,
        private val taskRepository: TaskRepository,
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
    fun findAllUserTasks(): List<Task> {
        return taskRepository.find(TaskOwnerIdIs(CurrentTaskOwnerIdHolder.currentTaskOwnerId)).toList()
    }

    @Transactional
    fun mergeUserTasks(idOfTaskToBeMergedIntoTheOtherOne: TaskId, idOfTaskToKeep: TaskId): Task {
        if (idOfTaskToBeMergedIntoTheOtherOne == idOfTaskToKeep) {
            throw TasksCanNotBeMergedException("Can't merge task ${idOfTaskToBeMergedIntoTheOtherOne.value} into itself")
        }

        val taskToBeMergedIntoTheOtherOne = findUserTaskOrThrow(idOfTaskToBeMergedIntoTheOtherOne)
        val taskToKeep = findUserTaskOrThrow(idOfTaskToKeep)

        return taskMergeService.mergeTasks(taskToBeMergedIntoTheOtherOne, taskToKeep)
    }

    private fun findUserTaskOrThrow(taskId: TaskId): Task {
        val taskToKeep = taskRepository.find(taskId) ?: throw TaskNotFoundException(taskId)
        if (taskToKeep.ownerId != CurrentTaskOwnerIdHolder.currentTaskOwnerId) {
            throw TasksCanNotBeMergedException("Task ${taskToKeep.id.value} doesn't belong to current user")
        }
        return taskToKeep
    }
}