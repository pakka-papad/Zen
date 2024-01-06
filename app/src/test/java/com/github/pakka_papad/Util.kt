package com.github.pakka_papad

import kotlin.test.assertContains
import kotlin.test.assertEquals

fun <T> assertCollectionEquals(expected: Collection<T>, actual: Collection<T>) {
    assertEquals(expected.size, actual.size)
    actual.forEach { assertContains(expected, it) }
    expected.forEach { assertContains(actual, it) }
}