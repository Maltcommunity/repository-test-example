package com.malt.task

import com.malt.task.test.InMemoryTaskRepository
import com.malt.test.time.SettableClock

class TaskCommandsFixtures {

    val clock by lazy { SettableClock() }
    val repository by lazy { InMemoryTaskRepository() }
    val currentUserTaskService by lazy { CurrentUserTaskService(clock, repository) }

    companion object {
        val ownerIdOfCurrentUser = TaskOwnerId("owner-id-of-current-user")

        init {
            CurrentTaskOwnerIdHolder.define(ownerIdOfCurrentUser)
        }
    }
}