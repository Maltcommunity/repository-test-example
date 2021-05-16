package com.malt.test.strikt

import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

internal class IsEqualToComparingPropertiesTest {

    @Test
    fun `should pass when values of all public properties are equal`() {
        // given
        val propHolder1 = SomePropHolder(
                someIntVar = 42,
                someStringVal = "foo",
                somePrivateVal = "xyz"
        )
        val propHolder2 = SomePropHolder(
                someIntVar = 42,
                someStringVal = "foo",
                somePrivateVal = "xyz"
        )
        expectThat(propHolder1) isNotEqualTo propHolder2

        // then
        expectThat(propHolder1) isEqualToComparingProperties propHolder2
    }

    @Test
    fun `should fail when values of at least one public property aren't equal`() {
        // given
        val propHolder1 = SomePropHolder(
                someIntVar = 42,
                someStringVal = "foo",
                somePrivateVal = "xyz"
        )
        val propHolder2 = SomePropHolder(
                someIntVar = 1,
                someStringVal = "foo",
                somePrivateVal = "xyz"
        )

        expectCatching {
            // when
            expectThat(propHolder1) isEqualToComparingProperties propHolder2
        }
                // then
                .isFailure()
                .isA<AssertionError>()
                .message isEqualTo """
                    |▼ Expect that SomePropHolder(someIntVar=42, someStringVal='foo', somePrivateVal='xyz'):
                    |  ✗ is equal to SomePropHolder(someIntVar=1, someStringVal='foo', somePrivateVal='xyz') by comparing properties
                    |                 someIntVar: expected=1, actual=42
                """.trimMargin()
    }

    @Test
    fun `should not fail when values of a private property aren't equal`() {
        // given
        val propHolder1 = SomePropHolder(
                someIntVar = 42,
                someStringVal = "foo",
                somePrivateVal = "xyz"
        )
        val propHolder2 = SomePropHolder(
                someIntVar = 42,
                someStringVal = "foo",
                somePrivateVal = "ABC"
        )

        // then
        expectCatching {
            expectThat(propHolder1) isEqualToComparingProperties propHolder2
        }.isSuccess()
    }
}

internal class SomePropHolder(
        var someIntVar: Int,
        val someStringVal: String,
        private val somePrivateVal: String
) {
    override fun toString(): String {
        return "SomePropHolder(" +
                "someIntVar=$someIntVar, " +
                "someStringVal='$someStringVal', " +
                "somePrivateVal='$somePrivateVal')"
    }
}
