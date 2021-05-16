package com.malt.task

import com.malt.task.TaskCommandsFixtures.Companion.ownerIdOfCurrentUser
import com.malt.task.test.TaskBuilder
import com.malt.test.strikt.isEqualToComparingProperties
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.map
import kotlin.streams.toList

internal class CurrentUserTaskServiceTest {

    val fixtures = TaskCommandsFixtures()
    val clock = fixtures.clock
    val repository = fixtures.repository
    val sut = fixtures.currentUserTaskService

    @Nested
    inner class AddTaskForUser {

        @Test
        fun `should save a task and return a representation of it`() {
            // given
            val taskSummary = "Some task"
            val taskDescription = "the aim of it being to do something"
            val expectedCreationDate = clock.stop().offsetDateTime

            // when
            val createdTaskReturned = sut.addTaskForUser(summary = taskSummary, description = taskDescription)

            // then
            val userTasks = repository.find(TaskOwnerIdIs(ownerIdOfCurrentUser)).toList()
            expectThat(userTasks) hasSize 1

            val createdTaskFetchedFromRepository = userTasks[0]
            expectThat(createdTaskFetchedFromRepository) {
                isEqualToComparingProperties(createdTaskReturned)
                get { ownerId } isEqualTo ownerIdOfCurrentUser
                get { creationDate } isEqualTo expectedCreationDate
                get { summary } isEqualTo taskSummary
                get { description } isEqualTo taskDescription
            }
        }
    }

    @Nested
    inner class FindAllUserTasks {

        @Test
        fun `should only return tasks of current user`() {
            // given
            val taskId1 = givenExistingTaskBelongingTo(ownerIdOfCurrentUser)
            givenExistingTaskBelongingTo(TaskOwnerId("some-other-owner"))
            val taskId3 = givenExistingTaskBelongingTo(ownerIdOfCurrentUser)
            val taskId4 = givenExistingTaskBelongingTo(ownerIdOfCurrentUser)
            givenExistingTaskBelongingTo(TaskOwnerId("yet-another-owner"))

            // when
            val actualTasks = sut.findAllUserTasks()

            // then
            expectThat(actualTasks.toList()).map { it.id }
                    .containsExactly(taskId1, taskId3, taskId4)
        }

        private fun givenExistingTaskBelongingTo(ownerId: TaskOwnerId): TaskId {
            val task = TaskBuilder(ownerId = ownerId).build()
            repository.save(task)
            return task.id
        }
    }
}