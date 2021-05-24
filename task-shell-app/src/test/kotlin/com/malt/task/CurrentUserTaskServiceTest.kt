package com.malt.task

import com.malt.task.TaskCommandsFixtures.Companion.ownerIdOfCurrentUser
import com.malt.test.strikt.isEqualToComparingProperties
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import kotlin.streams.toList

internal class CurrentUserTaskServiceTest {

    val fixtures = TaskCommandsFixtures()
    val clock = fixtures.clock
    val repository = fixtures.taskRepository
    val sut = fixtures.currentUserTaskService

    @Nested
    inner class AddTaskForUser {

        @Test
        fun `should save task and return it when adding a new task`() {
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

        private fun givenExistingTaskBelongingTo(ownerId: TaskOwnerId) =
                fixtures.givenAnExistingTask(ownerId = ownerId).id
    }

    @Nested
    inner class MergeUserTasks {

        @Test
        fun `should complain when first task can't be found`() {
            // given
            val taskId1 = TaskId("unknown-task-id")
            val taskId2 = givenAnExistingTaskBelongingToCurrentUser().id

            expectCatching {
                // when
                sut.mergeUserTasks(taskId1, taskId2)
            }   // then
                    .isFailure()
                    .isA<TaskNotFoundException>()
                    .message isEqualTo "Task not found: ${taskId1.value}"
        }

        @Test
        fun `should complain when second task can't be found`() {
            // given
            val taskId1 = givenAnExistingTaskBelongingToCurrentUser().id
            val taskId2 = TaskId("unknown-task-id")

            expectCatching {
                // when
                sut.mergeUserTasks(taskId1, taskId2)
            }   // then
                    .isFailure()
                    .isA<TaskNotFoundException>()
                    .message isEqualTo "Task not found: ${taskId2.value}"
        }

        @Test
        fun `should complain when provided tasks are the same one`() {
            // given
            val taskId = givenAnExistingTaskBelongingToCurrentUser().id

            expectCatching {
                // when
                sut.mergeUserTasks(taskId, taskId)
            }   // then
                    .isFailure()
                    .isA<TasksCanNotBeMergedException>()
                    .message isEqualTo "Can't merge task ${taskId.value} into itself"
        }

        @Test
        fun `should complain when first task doesn't belong to current owner`() {
            // given
            val taskId1 = fixtures.givenAnExistingTask(ownerId = TaskOwnerId("not-current-user")).id
            val taskId2 = givenAnExistingTaskBelongingToCurrentUser().id

            expectCatching {
                // when
                sut.mergeUserTasks(taskId1, taskId2)
            }   // then
                    .isFailure()
                    .isA<TasksCanNotBeMergedException>()
                    .message isEqualTo "Task ${taskId1.value} doesn't belong to current user"
        }

        @Test
        fun `should complain when second task doesn't belong to current owner`() {
            // given
            val taskId1 = givenAnExistingTaskBelongingToCurrentUser().id
            val taskId2 = fixtures.givenAnExistingTask(ownerId = TaskOwnerId("not-current-user")).id

            expectCatching {
                // when
                sut.mergeUserTasks(taskId1, taskId2)
            }   // then
                    .isFailure()
                    .isA<TasksCanNotBeMergedException>()
                    .message isEqualTo "Task ${taskId2.value} doesn't belong to current user"
        }

        fun givenAnExistingTaskBelongingToCurrentUser() =
                fixtures.givenAnExistingTask(ownerId = ownerIdOfCurrentUser)
    }
}