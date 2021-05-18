package com.malt.task

import com.malt.task.TaskCommandsFixtures.Companion.ownerIdOfCurrentUser
import com.malt.task.test.TaskBuilder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.streams.toList

internal class TaskCommandsTest {

    val fixtures = TaskCommandsFixtures()
    val repository = fixtures.repository
    val sut = TaskCommands(fixtures.currentUserTaskService)

    @Nested
    inner class AddTaskCommand {

        @Test
        fun `should save a task and return a representation of it`() {
            // given
            val taskSummary = "Some task"
            val taskDescription = "the aim of it being to do something"

            // when
            val resultingDisplay = sut.addTask(summary = taskSummary, description = taskDescription)

            // then
            val createdTask = findFirstTaskOfUser()

            expectThat(resultingDisplay) isEqualTo """
                Task ${createdTask.id.value} created with:
                Summary: $taskSummary
                Description: $taskDescription
            """.trimIndent()
        }

        @Test
        fun `should display an error in case task is invalid`() {
            // given
            val invalidTaskSummary = "  "

            // when
            val resultingDisplay = sut.addTask(summary = invalidTaskSummary, description = "irrelevant")

            // then
            expectThat(resultingDisplay) isEqualTo "Sorry, the provided summary is invalid. " +
                    "Task summary can't be blank."
        }

        @Test
        fun `should return appropriate representation when task has no description`() {
            // given
            val taskSummary = "Some task"
            val taskDescription = null

            // when
            val resultingDisplay = sut.addTask(summary = taskSummary, description = taskDescription)

            // then
            val createdTask = findFirstTaskOfUser()

            expectThat(resultingDisplay) isEqualTo """
                Task ${createdTask.id.value} created with:
                Summary: $taskSummary
                No description
            """.trimIndent()
        }

        private fun findFirstTaskOfUser() =
                repository.find(TaskOwnerIdIs(ownerIdOfCurrentUser)).toList().first()
    }

    @Nested
    inner class ListTasksCommand {

        @Test
        fun `should tell when there are no tasks to list`() {
            // given
            repository.clear()  // just to make the intention clear as the repository is initially empty

            // when
            val resultingDisplay = sut.listTasks(oneLinePerTask = false)

            // then
            expectThat(resultingDisplay) isEqualTo "Nothing to do right now!"
        }

        @Test
        fun `should display a representation of each task owned by current user`() {
            // given
            givenExistingTaskOfCurrentUser(1)
            givenExistingTaskOfCurrentUser(2)
            givenExistingTaskOfCurrentUser(3)

            // when
            val resultingDisplay = sut.listTasks(oneLinePerTask = false)

            // then
            expectThat(resultingDisplay) isEqualTo """
                task-id-1
                summary of task 1
                description of task 1
                
                task-id-2
                summary of task 2
                description of task 2
                
                task-id-3
                summary of task 3
                description of task 3
            """.trimIndent()
        }

        @Test
        fun `should display appropriate representation when a task has no description`() {
            // given
            givenExistingTaskOfCurrentUser(id = 42, description = null)

            // when
            val resultingDisplay = sut.listTasks(oneLinePerTask = false)

            // then
            expectThat(resultingDisplay) isEqualTo """
                task-id-42
                summary of task 42
                No description
            """.trimIndent()
        }

        private fun givenExistingTaskOfCurrentUser(id: Int, description: String? = "description of task $id") {
            val task = TaskBuilder(
                    id = TaskId("task-id-$id"),
                    ownerId = ownerIdOfCurrentUser,
                    summary = "summary of task $id",
                    description = description
            ).build()
            repository.save(task)
        }
    }
}
