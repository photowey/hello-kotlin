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

import kotlin.reflect.KClass

/**
 * {@code SwitchScope}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/11
 */
class SwitchScope<T>(val value: T) {
    var matched = false

    inline fun <reified A : T> case(action: A.() -> Unit) {
        if (!matched && value is A) {
            action(value)
            matched = true
        }
    }

    fun default(action: T.() -> Unit) {
        if (!matched) {
            action(value)
            matched = true
        }
    }
}

// ----------------------------------------------------------------

@Deprecated("Use SwitchScope instead")
class SwitchScope2<T>(val value: T) {
    private var matched = false

    fun <S : Any> case(kclazz: KClass<S>, through: Boolean = false, action: S.() -> Unit) {
        if ((!matched || through) && kclazz.isInstance(value)) {
            @Suppress("UNCHECKED_CAST")
            (value as S).action()
            if (!through) matched = true
        }
    }

    fun default(action: T.() -> Unit) {
        if (!matched) {
            action(value)
            matched = true
        }
    }
}


// ----------------------------------------------------------------

inline fun <T> switch(value: T, block: SwitchScope<T>.() -> Unit) {
    SwitchScope(value).apply(block)
}

// ----------------------------------------------------------------

inline fun <T> switch2(value: T, block: SwitchScope2<T>.() -> Unit) {
    SwitchScope2(value).apply(block)
}

// ----------------------------------------------------------------
