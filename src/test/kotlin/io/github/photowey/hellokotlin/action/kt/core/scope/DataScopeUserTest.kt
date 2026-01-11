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
package io.github.photowey.hellokotlin.action.kt.core.scope

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * {@code DataScopeUserTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/10
 */
class DataScopeUserTest {

    @Test
    fun testScopeFunctions() {
        val user = DataScopeUser(
            10086L,
            "photowey",
            18
        )

        // ----------------------------------------------------------------

        val letRvt = user.let { innerUser: DataScopeUser ->
            "let::${innerUser.javaClass}"
        }

        val letVal = "let::class io.github.photowey.hellokotlin.action.kt.core.domain.scope.DataScopeUser"
        assertEquals(letVal, letRvt)

        val runRvt = user.run {
            "run::${this.javaClass}"
        }
        val runVal = "run::class io.github.photowey.hellokotlin.action.kt.core.domain.scope.DataScopeUser"
        assertEquals(runVal, runRvt)

        // ----------------------------------------------------------------

        user.also {
            println("also::${it.javaClass}")
        }
        user.apply {
            println("apply::${this.javaClass}")
        }

        // ----------------------------------------------------------------

        user.takeIf { it.age > 16 }?.also { println("The name is:${it.name}!") } ?: println("The name is empty!")
        user.takeUnless { it.age > 16 }?.also { println("The name is empty!") } ?: println("The name is:${user.name}!")

        repeat(2) {
            print(it)
            println(user.name)
        }

        with(user) {
            this.name = "Tom"
        }

        assertEquals("Tom", user.name)
    }
}
