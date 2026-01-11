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
package io.github.photowey.hellokotlin.action.kt.eventbus.impl

import io.github.photowey.hellokotlin.action.kt.eventbus.model.Event
import io.github.photowey.hellokotlin.action.kt.eventbus.rpc.reply
import io.github.photowey.hellokotlin.action.kt.eventbus.rpc.request
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * {@code TrieEventBusTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/11
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TrieEventBusTest {

    private val testScope = CoroutineScope(Dispatchers.Default)

    @Test
    fun testBasicPostAndSubscribe() = runTest {
        val bus = TrieEventBus(testScope)
        val counter = AtomicInteger(0)

        bus.subscribe("basic.topic") { counter.incrementAndGet() }
        bus.post(Event(topic = "basic.topic"))
        advanceUntilIdle()

        assertEquals(1, counter.get(), "First post should trigger the event")
    }

    @Test
    fun testPriorityOrder() = runTest {
        val bus = TrieEventBus(testScope)
        val results = mutableListOf<Int>()

        bus.subscribe("priority.topic", priority = 5) { results.add(5) }
        bus.subscribe("priority.topic", priority = 10) { results.add(10) }
        bus.subscribe("priority.topic", priority = 1) { results.add(1) }

        bus.post(Event(topic = "priority.topic"))
        advanceUntilIdle()

        assertEquals(
            listOf(10, 5, 1),
            results, "Handlers should be invoked in descending priority"
        )
    }

    @Test
    fun testCancelPropagation() = runTest {
        val bus = TrieEventBus(testScope)
        val results = mutableListOf<Int>()

        bus.subscribe("cancel.topic", priority = 5) {
            results.add(1)
            it.cancel()
        }
        bus.subscribe("cancel.topic", priority = 1) {
            results.add(2)
        }

        bus.post(Event(topic = "cancel.topic"))
        advanceUntilIdle()

        assertEquals(
            listOf(1),
            results,
            "Subsequent handlers should not run after cancel"
        )
    }

    @Test
    fun testUnsubscribePhysicalRemoval() = runTest {
        val bus = TrieEventBus(testScope)

        val counter = AtomicInteger(0)
        val handle = bus.subscribe("user.*") { counter.incrementAndGet() }

        bus.post(Event(topic = "user.login"))
        advanceUntilIdle()
        assertEquals(1, counter.get(), "First post should trigger handler")

        handle.unsubscribe()

        bus.post(Event(topic = "user.login"))
        advanceUntilIdle()
        assertEquals(1, counter.get(), "Handler should not trigger after unsubscribe")
    }

    @Test
    fun testWildcardMatching() = runTest {
        val bus = TrieEventBus(testScope)
        val results = mutableListOf<String>()

        bus.subscribe("user.*") { results.add("star") }
        bus.subscribe("user.#") { results.add("hash") }

        bus.post(Event(topic = "user.login"))
        bus.post(Event(topic = "user.profile.update"))
        advanceUntilIdle()

        assertEquals(
            listOf("star", "hash", "hash"),
            results, "Wildcard matching should work correctly"
        )
    }

    @Test
    fun testStopClearAndRestart() = runTest {
        val bus = TrieEventBus(testScope)
        val counter = AtomicInteger(0)

        bus.subscribe("restart.topic") { counter.incrementAndGet() }
        bus.post(Event(topic = "restart.topic"))
        advanceUntilIdle()
        assertEquals(1, counter.get(), "First post should trigger the event")

        bus.stop()
        bus.post(Event(topic = "restart.topic"))
        advanceUntilIdle()
        assertEquals(1, counter.get(), "Post after stop should not trigger the event")

        bus.clear()
        bus.subscribe("restart.topic") { counter.incrementAndGet() }
        bus.post(Event(topic = "restart.topic"))
        advanceUntilIdle()
        assertEquals(
            1,
            counter.get(),
            "Post after stop + clear should not trigger the event"
        )

        bus.restart()
        bus.post(Event(topic = "restart.topic"))
        advanceUntilIdle()
        assertEquals(2, counter.get(), "Post after restart should trigger the event")
    }

    @Test
    fun testRpcRequestReply() = runTest {
        val bus = TrieEventBus(testScope)

        bus.subscribe("rpc.test") { ctx ->
            val payload = ctx.event.data
            if (payload is Pair<*, *>) {
                ctx.reply("reply-${payload.first}")
            }
        }

        val responseDeferred: String = withContext(Dispatchers.Default.limitedParallelism(1)) {
            bus.request("rpc.test", "hello")
        }

        assertEquals("reply-hello", responseDeferred, "RPC should reply correctly")
    }

    @Test
    fun testRpcTimeout() = runTest {
        val bus = TrieEventBus(testScope)

        bus.subscribe("rpc.timeout") { }

        assertFailsWith<TimeoutCancellationException> {
            bus.request<String>("rpc.timeout", "test", timeoutMillis = 100)
        }
    }

    @Test
    fun testHashOnly() = runTest {
        val bus = TrieEventBus(testScope)
        val results = mutableListOf<String>()

        bus.subscribe("#") { results.add("all") }

        bus.post(Event(topic = "a"))
        bus.post(Event(topic = "a.b"))
        bus.post(Event(topic = "a.b.c"))
        advanceUntilIdle()

        assertEquals(
            listOf("all", "all", "all"),
            results,
            "# should match any topic depth"
        )
    }

    @Test
    fun testStarDoesNotMatchZeroToken() = runTest {
        val bus = TrieEventBus(testScope)
        val results = mutableListOf<String>()

        bus.subscribe("user.*") { results.add("hit") }

        bus.post(Event(topic = "user"))
        advanceUntilIdle()

        assertTrue(results.isEmpty(), "* should not match zero token")
    }

    @Test
    fun testMixedWildcard() = runTest {
        val bus = TrieEventBus(testScope)
        val results = mutableListOf<String>()

        bus.subscribe("user.*.update") { results.add("star") }
        bus.subscribe("user.#.update") { results.add("hash") }

        bus.post(Event(topic = "user.profile.update"))
        bus.post(Event(topic = "user.a.b.c.update"))
        advanceUntilIdle()

        assertEquals(
            listOf("star", "hash", "hash"),
            results,
            "Mixed wildcard should match correctly"
        )
    }

    @Test
    fun testInvalidMultipleHash() {
        val bus = TrieEventBus(testScope)

        bus.subscribe("user.*.update") { }
        bus.subscribe("user.#.update") { }
        bus.subscribe("*.update") { }
        bus.subscribe("user.#") { }
        bus.subscribe("*") { }
        bus.subscribe("#") { }

        assertThrows<IllegalArgumentException> {
            bus.subscribe("user.#.#") { }
        }
        assertThrows<IllegalArgumentException> {
            bus.subscribe("user.*.#") { }
        }
    }

    @Test
    fun testHashNotDuplicated() = runTest {
        val bus = TrieEventBus(testScope)
        val counter = AtomicInteger(0)

        bus.subscribe("user.#") { counter.incrementAndGet() }

        bus.post(Event(topic = "user.profile.update"))
        advanceUntilIdle()

        assertEquals(1, counter.get(), "Hash wildcard must not duplicate")
    }
}
