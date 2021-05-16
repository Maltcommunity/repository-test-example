package com.malt.task.test

import com.malt.task.TaskRepositoryContract

class InMemoryTaskRepositoryTest : TaskRepositoryContract() {
    override fun buildRepositoryUnderTest() = InMemoryTaskRepository()
}