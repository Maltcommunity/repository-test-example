package com.malt.task

import com.malt.task.test.TaskFixtures
import com.malt.test.strikt.isEqualToComparingProperties
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

internal class TaskMergeServiceTest {

    val fixtures = TaskFixtures()
    val repository = fixtures.taskRepository
    val sut = fixtures.taskMergeService

    @Test
    fun `should complain when tasks don't have the same owner`() {
        // given
        val task1 = fixtures.givenAnExistingTask(ownerId = TaskOwnerId("task-owner-1"))
        val task2 = fixtures.givenAnExistingTask(ownerId = TaskOwnerId("task-owner-2"))

        expectCatching {
            // when
            sut.mergeTasks(task1, task2)
        }   // then
                .isFailure()
                .isA<TasksCanNotBeMergedException>()
                .message isEqualTo "${task1.id} and ${task2.id} have different owners"
    }

    @Test
    fun `should complain when provided tasks are the same one`() {
        // given
        val task = fixtures.givenAnExistingTask()

        expectCatching {
            // when
            sut.mergeTasks(task, task)
        }   // then
                .isFailure()
                .isA<TasksCanNotBeMergedException>()
                .message isEqualTo "Can't merge ${task.id} into itself"
    }

    @Test
    fun `should merge first task into the second one`() {
        // given
        val task1 = fixtures.givenAnExistingTask(summary = "task 1", description = "desc of task 1")
        val task2 = fixtures.givenAnExistingTask(summary = "task 2", description = "desc of task 2")

        // when
        val mergedTask = sut.mergeTasks(task1, task2)

        // then
        expectThat(repository.find(task1.id)).isNull()
        expectThat(repository.find(task2.id))
                .isEqualToComparingProperties(mergedTask)
                .isEqualToComparingProperties(Task(
                        id = task2.id,
                        creationDate = task2.creationDate,
                        ownerId = task2.ownerId,
                        summary = "${task2.summary} - ${task1.summary}",
                        description = """
                            ${task2.description}
                            
                            ${task1.description}
                        """.trimIndent()
                ))
    }
}