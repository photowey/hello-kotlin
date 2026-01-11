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
package io.github.photowey.hellokotlin.action.kt.core.coroutine

import io.github.photowey.hellokotlin.action.kt.core.coroutine.switch
import io.github.photowey.hellokotlin.action.kt.core.coroutine.switch2
import org.junit.jupiter.api.Test

/**
 * {@code SwitchScopeTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/10
 */
class SwitchScopeTest {

    @Test
    fun testSwitchScope() {
        open class Animal {
            fun speak() = println("Animal")
        }

        class Dog : Animal() {
            fun bark() = println("Bark!")
        }

        class Cat : Animal() {
            fun meow() = println("Meow!")
        }

        val pet: Animal = Dog()

        switch(pet) {
            case<Dog> { bark() }
            case<Cat> { meow() }
            default { speak() }
        }
    }

    @Test
    fun testSwitchScope_2() {
        val state: Any = 42

        switch2(state) {
            case(String::class) { println("string") }
            case(Int::class, through = true) { println("int") }
            case(Number::class) { println("number") }
            default { println("other") }
        }
    }
}
