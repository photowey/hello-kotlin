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
package io.github.photowey.hellokotlin.action.kt.core.dynamicproxy

import io.github.photowey.hellokotlin.action.kt.core.dynamicproxy.Dog
import io.github.photowey.hellokotlin.action.kt.core.dynamicproxy.Zoo
import io.github.photowey.hellokotlin.action.kt.core.dynamicproxy.ZooOr
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * {@code AnimalKtTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/10
 */
class AnimalKtTest {

    @Test
    fun testZooBark() {
        val zoo = Zoo(Dog())
        assertEquals("Hello, Dog!", zoo.bark())
    }

    @Test
    fun testZooOrBark() {
        val zoo = ZooOr(Dog())
        assertEquals("Hello, ZooOr!", zoo.bark())
    }
}
