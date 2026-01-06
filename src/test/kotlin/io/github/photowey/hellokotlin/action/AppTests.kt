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
package io.github.photowey.hellokotlin.action

import io.github.photowey.hellokotlin.action.core.domain.Hello
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * {@code AppTests}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/06
 */
class AppTests {

    @Test
    fun testGreeting_java() {
        val hello = Hello("Kotlin")
        Assertions.assertEquals("Hello, Kotlin!", hello.greeting())
    }

    @Test
    fun testGreeting_kotlin() {
        val hello = Hello("Kotlin")
        assertEquals("Hello, Kotlin!", hello.greeting())
    }
}
