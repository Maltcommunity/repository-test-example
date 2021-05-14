package com.malt.task

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SomeTest {

    @Test
    fun `should test something`() {
        expectThat("foo") isEqualTo "foo"
    }
}