package com.malt.task

import com.malt.task.test.InMemoryTaskRepository
import com.malt.test.time.SettableClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import kotlin.streams.toList

private val ownerIdOfCurrentUser = TaskOwnerId("some-owner-id")

internal class TaskCommandsTest {

    val clock = SettableClock()
    val repository = InMemoryTaskRepository()
    val sut = TaskCommands(clock, repository)

    @BeforeEach
    fun defineCurrentUser() {
        if (!CurrentTaskOwnerIdHolder.isDefined) {
            CurrentTaskOwnerIdHolder.define(ownerIdOfCurrentUser)
        }
    }

    @Nested
    inner class AddTaskCommand {

        @Test
        fun `should save a task and return a representation of it`() {
            // given
            val taskSummary = "Some task"
            val taskDescription = "the aim of it being to do something"
            val expectedCreationDate = clock.stop().offsetDateTime

            // when
            val resultingDisplay = sut.addTask(summary = taskSummary, description = taskDescription)

            // then
            val userTasks = repository.find(TaskOwnerIdIs(ownerIdOfCurrentUser)).toList()
            expectThat(userTasks) hasSize 1

            val createdTask = userTasks[0]
            expectThat(createdTask) {
                get { ownerId } isEqualTo ownerIdOfCurrentUser
                get { creationDate } isEqualTo expectedCreationDate
                get { summary } isEqualTo taskSummary
                get { description } isEqualTo taskDescription
            }

            expectThat(resultingDisplay) isEqualTo """
                Task ${createdTask.id.value} created with:
                Summary: $taskSummary
                Description: $taskDescription
            """.trimIndent()
        }
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
            givenExistingTask(1, ownerIdOfCurrentUser)
            givenExistingTask(2, TaskOwnerId("some-other-owner"))
            givenExistingTask(3, ownerIdOfCurrentUser)
            givenExistingTask(4, ownerIdOfCurrentUser)
            givenExistingTask(5, TaskOwnerId("yet-another-owner"))

            // when
            val resultingDisplay = sut.listTasks(oneLinePerTask = false)

            // then
            expectThat(resultingDisplay) isEqualTo """
                task-id-1
                summary of task 1
                description of task 1
                
                task-id-3
                summary of task 3
                description of task 3
                
                task-id-4
                summary of task 4
                description of task 4
            """.trimIndent()
        }

        private fun givenExistingTask(id: Int, ownerId: TaskOwnerId) {
            repository.save(Task(
                    id = TaskId("task-id-$id"),
                    ownerId = ownerId,
                    summary = "summary of task $id",
                    description = "description of task $id"))
        }
    }
}