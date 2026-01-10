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
package io.github.photowey.hellokotlin.action.kt.core.lambda;

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * {@code FunctionsKtTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/07
 */
class FunctionsKtTest {

    @Test
    fun testFunction_22() {
        val echo22 = { a1: Int, a2: Int, a3: Int, a4: Int, a5: Int,
                       a6: Int, a7: Int, a8: Int, a9: Int, a10: Int,
                       a11: Int, a12: Int, a13: Int, a14: Int, a15:
                       Int, a16: Int, a17: Int, a18: Int, a19: Int, a20: Int,
                       a21: Int, a22: Int ->
            a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + a10 +
                a11 + a12 + a13 + a14 + a15 + a16 + a17 + a18 + a19 + a20 +
                a21 + a22
        }

        val result = echo22(
            1, 2, 3, 4, 5,
            6, 7, 8, 9, 10,
            11, 12, 13, 14, 15,
            16, 17, 18, 19, 20,
            21, 22
        )

        assertEquals(253, result)
    }

    @Test
    fun testFunction_23() {
        val echo23 = { a1: Int, a2: Int, a3: Int, a4: Int, a5: Int,
                       a6: Int, a7: Int, a8: Int, a9: Int, a10: Int,
                       a11: Int, a12: Int, a13: Int, a14: Int, a15:
                       Int, a16: Int, a17: Int, a18: Int, a19: Int, a20: Int,
                       a21: Int, a22: Int, a23: Int ->
            a1 + a2 + a3 + a4 + a5 +
                a6 + a7 + a8 + a9 + a10 +
                a11 + a12 + a13 + a14 + a15 +
                a16 + a17 + a18 + a19 + a20 +
                a21 + a22 + a23
        }

        val result = echo23(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23
        )

        assertEquals(276, result)
    }
}
