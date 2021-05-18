package com.malt.task

import com.malt.task.test.TaskBuilder
import com.malt.test.strikt.isEqualToComparingProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEmpty
import strikt.assertions.isNull
import strikt.assertions.map
import kotlin.streams.toList

// While the domain should generally be agnostic to any framework, and while this contract
// specifically shouldn't know about @Transactional (it only concerns a specific implementation),
// the inner workings of Spring's TransactionalTestExecutionListener force us to do so, as putting
// @Transactional on the specific test class concerned would only decorate its own test methods.
//
// An alternative would be to 1) annotate the specific test class and 2) override all test methods
// of this class - while still delegating to it - so that they are decorated by Spring. But this
// isn't convenient, really. Therefore, as we don't care much depending on Spring for our tests -
// as long as we don't depend on it within our "production" domain code - we accept this tradeoff.
//
// As a side note: we're considering proposing an improvement to the Spring team on that matter.
@Transactional
abstract class TaskRepositoryContract {

    abstract fun buildRepositoryUnderTest(): TaskRepository

    private val sut by lazy { buildRepositoryUnderTest() }

    @Test
    fun `should find a saved task using its ID`() {
        // given
        val unsavedTask = TaskBuilder().build()
        // then
        expectThat(sut.find(unsavedTask.id)).isNull()

        // when
        sut.save(unsavedTask)
        // then
        assertThat(sut.find(unsavedTask.id)).usingRecursiveComparison().isEqualTo(unsavedTask)
    }

    @Test
    fun `should save a task having a minimum of details`() {
        // given
        val unsavedTask = TaskBuilder(
                summary = "A",
                description = null
        ).build()

        // when
        sut.save(unsavedTask)

        // then
        assertThat(sut.find(unsavedTask.id)).usingRecursiveComparison().isEqualTo(unsavedTask)
    }

    @Test
    fun `should update an existing task`() {
        // given
        val initialTask = TaskBuilder().build()
        sut.save(initialTask)

        // when
        val updatedTask = initialTask.withSummary("new summary")
        sut.save(updatedTask)

        // then
        expectThat(sut.find(initialTask.id)) isEqualToComparingProperties updatedTask
    }

    @Test
    fun `should find tasks using a specification`() {
        // given
        val ownerIdA = TaskOwnerId("a")
        val ownerIdB = TaskOwnerId("b")
        val task1 = givenATaskSavedWithOwner(ownerIdA)
        val task2 = givenATaskSavedWithOwner(ownerIdB)
        val task3 = givenATaskSavedWithOwner(ownerIdA)

        // then
        expectThat(findTasksBySpec(TaskOwnerIdIs(ownerIdA)))
                .map { it.id }
                .containsExactly(task1.id, task3.id)
        expectThat(findTasksBySpec(TaskOwnerIdIs(ownerIdB)))
                .map { it.id }
                .containsExactly(task2.id)
        expectThat(findTasksBySpec(!TaskOwnerIdIs(ownerIdA)))
                .map { it.id }
                .containsExactly(task2.id)
        expectThat(findTasksBySpec(TaskOwnerIdIs(ownerIdA) or TaskOwnerIdIs(ownerIdB)))
                .map { it.id }
                .containsExactly(task1.id, task2.id, task3.id)
        expectThat(findTasksBySpec(TaskOwnerIdIs(ownerIdA) and TaskOwnerIdIs(ownerIdB)))
                .isEmpty()
        expectThat(findTasksBySpec(TaskOwnerIdIs(ownerIdA) and TaskIdIs(task3.id)))
                .map { it.id }
                .containsExactly(task3.id)
    }

    @Test
    fun `should delete a task`() {
        // given
        val task1 = TaskBuilder().build()
        sut.save(task1)
        val task2 = TaskBuilder().build()
        sut.save(task2)

        // when
        sut.delete(task1.id)

        // then
        expectThat(sut.find(task1.id)).isNull()
        expectThat(sut.find(task2.id)) isEqualToComparingProperties task2
    }

    private fun findTasksBySpec(spec: TaskSpecification) = sut.find(spec).toList()

    private fun givenATaskSavedWithOwner(ownerId: TaskOwnerId): Task {
        val task = TaskBuilder(ownerId = ownerId).build()
        sut.save(task)
        return task
    }
}
