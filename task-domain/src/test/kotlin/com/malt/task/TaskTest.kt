package com.malt.task

import com.malt.task.test.TaskBuilder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThrows

internal class TaskTest {

    @Nested
    inner class Validation {

        @ParameterizedTest(name = "blank summary at construction [{index}] summary=\"{0}\"")
        @ValueSource(strings = ["", " ", "   \n "])
        fun `should reject a blank summary at construction`(blankSummary: String) {
            expectThrows<IllegalArgumentException> {
                TaskBuilder(summary = blankSummary).build()
            }
        }

        @ParameterizedTest(name = "blank summary at copy [{index}] summary=\"{0}\"")
        @ValueSource(strings = ["", " ", "   \n "])
        fun `should reject a blank summary `(blankSummary: String) {
            val validTask = TaskBuilder().build()

            expectThrows<IllegalArgumentException> {
                validTask.withSummary(blankSummary)
            }
        }
    }
}