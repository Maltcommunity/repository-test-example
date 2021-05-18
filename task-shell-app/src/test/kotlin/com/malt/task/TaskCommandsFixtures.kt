package com.malt.task

import com.malt.task.test.TaskFixtures

class TaskCommandsFixtures : TaskFixtures() {

    val currentUserTaskService by lazy { CurrentUserTaskService(clock, taskMergeService, taskRepository) }

    companion object {
        val ownerIdOfCurrentUser = TaskOwnerId("owner-id-of-current-user")

        init {
            CurrentTaskOwnerIdHolder.define(ownerIdOfCurrentUser)
        }
    }
}