package com.malt.test.strikt

import strikt.api.Assertion

import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

// TODO maybe:
//  1. support usual container types (*Array, Iterable, Optional...)
//  2. improve the syntax to this end:
//     expectThat(...)
//         .comparingProperties(maybeProperties...)
//         .recursively()
//         .isEqualTo(...)  // or for "containers" .contains*(...)
//  3. and contribute it back to Strikt
infix fun <T> Assertion.Builder<T>.isEqualToComparingProperties(expected: T?): Assertion.Builder<T> =
        assert("is equal to %s by comparing properties", expected) { actual ->
            when {
                actual == expected -> pass(actual = actual)
                expected == null -> fail(actual = actual)
                else -> {
                    @Suppress("UNCHECKED_CAST")
                    val firstPropNotEqual = actual!!::class.memberProperties
                            .filter { it.visibility == KVisibility.PUBLIC }
                            .map { it as KProperty1<T, *> }
                            .firstOrNull { it.get(actual) != it.get(expected) }

                    if (firstPropNotEqual == null) {
                        pass(actual = actual)
                    } else {
                        fail(actual = actual, description = "${firstPropNotEqual.name}: " +
                                "expected=${firstPropNotEqual.get(expected)}, " +
                                "actual=${firstPropNotEqual.get(actual)}")
                    }
                }
            }
        }
