/*
 * Copyright (c) 2026-present The Hello-Kotlin Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.photowey.hellokotlin.action.kt.core.domain.coroutine

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * {@code SequenceBuilderTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/10
 */
class SequenceBuilderTest {

    @Test
    fun testSequence() {
        val fibonacci = sequence {
            var current = 1
            var next = 1
            while (true) {
                yield(current)
                val tmp = current + next
                current = next
                next = tmp
            }
        }

        fibonacci
            .take(10)
            .forEach { print("$it ") }
    }

    @Test
    fun testSequence_boom() {
        val exception = assertThrows<IllegalStateException> {
            val seq = sequence {
                yield(1)
                error("boom")
            }

            seq.forEach { println(it) } // ⚡
        }

        println("Caught exception: ${exception.message}") // boom
    }

    @Test
    fun testSequenceYieldAll() {
        val seq = sequence {
            yield(1)
            yieldAll(listOf(2, 3))
            yieldAll(sequenceOf(4, 5))
            yieldAll {
                yield(6)
                yield(7)
            }
            yield(8)
        }

        seq.forEach { print("$it ") }
    }

    @Test
    fun testYieldAllWithException() {
        val seq = sequence {
            yield(1)
            yieldAll(listOf(2, 3))
            yieldAll(sequenceOf(4, 5))
            yieldAll {
                yield(6)
                yield(7)
            }
            yield(8)
            error("boom")
        }

        val iterator = seq.iterator()

        repeat(8) {
            print("${iterator.next()} ")
        }

        val exception = assertThrows<IllegalStateException> {
            iterator.next() // ⚡
        }

        println("\nCaught exception: ${exception.message}") // boom
    }
}
