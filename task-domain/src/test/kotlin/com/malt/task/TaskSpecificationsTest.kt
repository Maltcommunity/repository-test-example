package com.malt.task

import com.malt.task.test.TaskBuilder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isFalse
import strikt.assertions.isTrue

internal class TaskSpecificationsTest {

    val someOtherTaskId = TaskId.generate()
    val someOtherOwnerId = TaskOwnerId("some other owner id")

    @Nested
    inner class TaskIdIsSpecification {

        @Test
        fun `should match`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(task.id)
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should not match`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(someOtherTaskId)
            expectThat(spec isSatisfiedBy task).isFalse()
        }
    }

    @Nested
    inner class TaskOwnerIdIsSpecification {

        @Test
        fun `should match`() {
            val task = TaskBuilder().build()
            val spec = TaskOwnerIdIs(task.ownerId)
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should not match`() {
            val task = TaskBuilder().build()
            val spec = TaskOwnerIdIs(someOtherOwnerId)
            expectThat(spec isSatisfiedBy task).isFalse()
        }
    }

    @Nested
    inner class AndTaskSpecification {

        @Test
        fun `should match when both children match`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(task.id) and TaskOwnerIdIs(task.ownerId)
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should not match when only left child matches`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(task.id) and TaskOwnerIdIs(someOtherOwnerId)
            expectThat(spec isSatisfiedBy task).isFalse()
        }

        @Test
        fun `should not match when only right child matches`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(someOtherTaskId) and TaskOwnerIdIs(task.ownerId)
            expectThat(spec isSatisfiedBy task).isFalse()
        }

        @Test
        fun `should not match when no child matches`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(someOtherTaskId) and TaskOwnerIdIs(someOtherOwnerId)
            expectThat(spec isSatisfiedBy task).isFalse()
        }
    }

    @Nested
    inner class OrTaskSpecification {

        @Test
        fun `should match when both children match`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(task.id) or TaskOwnerIdIs(task.ownerId)
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should match when only left child matches`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(task.id) or TaskOwnerIdIs(someOtherOwnerId)
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should match when only right child matches`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(someOtherTaskId) or TaskOwnerIdIs(task.ownerId)
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should not match when no child matches`() {
            val task = TaskBuilder().build()
            val spec = TaskIdIs(someOtherTaskId) or TaskOwnerIdIs(someOtherOwnerId)
            expectThat(spec isSatisfiedBy task).isFalse()
        }
    }

    @Nested
    inner class NotTaskSpecification {

        @Test
        fun `should not match`() {
            val task = TaskBuilder().build()
            val spec = !TaskIdIs(task.id)
            expectThat(spec isSatisfiedBy task).isFalse()
        }

        @Test
        fun `should match`() {
            val task = TaskBuilder().build()
            val spec = !TaskIdIs(someOtherTaskId)
            expectThat(spec isSatisfiedBy task).isTrue()
        }
    }

    @Nested
    inner class AllOfTaskSpecification {

        @Test
        fun `should throw when called without specifications`() {
            expectThrows<IllegalArgumentException> {
                allOf()
            }
        }

        @Test
        fun `should match when all children match`() {
            val task = TaskBuilder().build()
            val spec = allOf(TaskIdIs(task.id), TaskOwnerIdIs(task.ownerId), TaskIdIs(task.id))
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should not match when one child doesn't match`() {
            val task = TaskBuilder().build()
            val spec = allOf(TaskIdIs(task.id), TaskOwnerIdIs(task.ownerId), TaskIdIs(someOtherTaskId))
            expectThat(spec isSatisfiedBy task).isFalse()
        }

        @Test
        fun `should not match when several children don't match`() {
            val task = TaskBuilder().build()
            val spec = allOf(TaskIdIs(task.id), TaskOwnerIdIs(someOtherOwnerId), TaskIdIs(someOtherTaskId))
            expectThat(spec isSatisfiedBy task).isFalse()
        }

        @Test
        fun `should not match when no child matches`() {
            val task = TaskBuilder().build()
            val spec = allOf(TaskIdIs(someOtherTaskId), TaskOwnerIdIs(someOtherOwnerId), TaskIdIs(someOtherTaskId))
            expectThat(spec isSatisfiedBy task).isFalse()
        }
    }

    @Nested
    inner class AnyOfTaskSpecification {

        @Test
        fun `should throw when called without specifications`() {
            expectThrows<IllegalArgumentException> {
                anyOf()
            }
        }

        @Test
        fun `should match when all children match`() {
            val task = TaskBuilder().build()
            val spec = anyOf(TaskIdIs(task.id), TaskOwnerIdIs(task.ownerId), TaskIdIs(task.id))
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should match when one child matches`() {
            val task = TaskBuilder().build()
            val spec = anyOf(TaskIdIs(task.id), TaskOwnerIdIs(someOtherOwnerId), TaskIdIs(someOtherTaskId))
            expectThat(spec isSatisfiedBy task).isTrue()
        }

        @Test
        fun `should not match when no child matches`() {
            val task = TaskBuilder().build()
            val spec = anyOf(TaskIdIs(someOtherTaskId), TaskOwnerIdIs(someOtherOwnerId), TaskIdIs(someOtherTaskId))
            expectThat(spec isSatisfiedBy task).isFalse()
        }
    }

    @Nested
    inner class NoneOfTaskSpecification {

        @Test
        fun `should throw when called without specifications`() {
            expectThrows<IllegalArgumentException> {
                noneOf()
            }
        }

        @Test
        fun `should not match when all children match`() {
            val task = TaskBuilder().build()
            val spec = noneOf(TaskIdIs(task.id), TaskOwnerIdIs(task.ownerId), TaskIdIs(task.id))
            println(spec)
            expectThat(spec isSatisfiedBy task).isFalse()
        }

        @Test
        fun `should not match when one child matches`() {
            val task = TaskBuilder().build()
            val spec = noneOf(TaskIdIs(task.id), TaskOwnerIdIs(someOtherOwnerId), TaskIdIs(someOtherTaskId))
            println(spec)
            expectThat(spec isSatisfiedBy task).isFalse()
        }

        @Test
        fun `should match when no child matches`() {
            val task = TaskBuilder().build()
            val spec = noneOf(TaskIdIs(someOtherTaskId), TaskOwnerIdIs(someOtherOwnerId), TaskIdIs(someOtherTaskId))
            println(spec)
            expectThat(spec isSatisfiedBy task).isTrue()
        }
    }
}