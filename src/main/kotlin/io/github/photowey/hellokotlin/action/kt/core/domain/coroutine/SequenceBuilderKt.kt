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
package io.github.photowey.hellokotlin.action.kt.core.domain.coroutine;

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.*

/**
 * {@code SequenceBuilderKt}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/10
 */
interface SequenceBuilderKt<in T> {

    suspend fun yield(value: T)

    suspend fun yieldAll(values: Iterable<T>) {
        for (v in values) yield(v)
    }

    suspend fun yieldAll(values: Sequence<T>) {
        for (v in values) yield(v)
    }

    suspend fun yieldAll(block: suspend SequenceBuilderKt<T>.() -> Unit) {
        val nested = SequenceCoroutine<T>()
        block.startCoroutine(nested, nested)
        while (nested.hasNext()) {
            yield(nested.next())
        }
    }
}


// ----------------------------------------------------------------

inline fun <T> Any._x_(block: () -> T): T = block()

fun <T> sequence(
    block: suspend SequenceBuilderKt<T>.() -> Unit
): Sequence<T> {
    return Sequence {
        SequenceCoroutine<T>().apply {
            next = block.createCoroutine(receiver = this, completion = this)
        }
    }
}

private class SequenceCoroutine<T> : AbstractIterator<T>(), SequenceBuilderKt<T>, Continuation<Unit> {
    private var state: State = State.Idle

    override val context: CoroutineContext = EmptyCoroutineContext
    lateinit var next: Continuation<Unit>

    // ----------------------------------------------------------------

    private sealed class State {
        object Idle : State()
        object Running : State()
        class Suspended(val cont: Continuation<Unit>) : State()
        object Done : State()
        class Failed(val exception: Throwable) : State()

    }

    // ----------------------------------------------------------------

    override fun computeNext() {
        when (val stat = state) {
            State.Idle -> {
                state = State.Running
                next.resume(Unit)
                trampolineCheck()
            }

            is State.Suspended -> {
                state = State.Running
                stat.cont.resume(Unit)
                trampolineCheck()
            }

            State.Done -> done()
            is State.Failed -> throw stat.exception
            State.Running -> error("Iterator is already running")
        }
    }

    override fun resumeWith(result: Result<Unit>) {
        state = result.fold(
            onSuccess = { State.Done },
            onFailure = { State.Failed(it) }
        )
    }

    override suspend fun yield(value: T) {
        setNext(value)
        return suspendCancellableCoroutine { cont ->
            state = State.Suspended(cont)
            cont.invokeOnCancellation {
                state = State.Done
            }
        }
    }

    // ----------------------------------------------------------------

    private fun trampolineCheck() {
        /*
         when (val stat = state) {
                State.Done -> done()
                is State.Failed -> {
                    throw stat.exception
                }

                else -> {
                    // fallthrough
                }
            }
        */

        switch(state) {
            case<State.Done> { done() }
            case<State.Failed> { throw exception }
            default {
                // fallthrough
            }
        }

        /*
        switch2(state) {
            case(State.Done::class) { done() }
            case(State.Failed::class) { throw exception }
            default {
                // fallthrough
            }
        }
        */
    }
}
