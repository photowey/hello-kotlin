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
package io.github.photowey.hellokotlin.action.kt.eventbus.impl;

import io.github.photowey.hellokotlin.action.kt.eventbus.api.EventBus
import io.github.photowey.hellokotlin.action.kt.eventbus.api.SubscriptionHandle
import io.github.photowey.hellokotlin.action.kt.eventbus.model.Event
import io.github.photowey.hellokotlin.action.kt.eventbus.model.EventContext
import io.github.photowey.hellokotlin.action.kt.eventbus.routing.Subscription
import io.github.photowey.hellokotlin.action.kt.eventbus.routing.TopicTrie
import io.github.photowey.hellokotlin.action.kt.eventbus.util.EventValidators
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * {@code TrieEventBus}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/11
 */
class TrieEventBus(
    private val scope: CoroutineScope
) : EventBus {

    private val trie = TopicTrie()
    private val stopped = AtomicBoolean(false)

    private val eventQueues = ConcurrentHashMap<String/*topic*/, ConcurrentLinkedQueue<Event>>()
    private val consumers = ConcurrentHashMap<String/*topic*/, Job>()

    // ------------------------------------------------------------

    override fun post(event: Event) {
        if (stopped.get()) return

        val queue = eventQueues.computeIfAbsent(event.topic) {
            ConcurrentLinkedQueue()
        }
        queue.add(event)

        startConsumerIfNeeded(event.topic)
    }

    override fun subscribe(
        topicPattern: String,
        priority: Int,
        handler: suspend (EventContext) -> Unit
    ): SubscriptionHandle {
        EventValidators.validateTopicPattern(topicPattern)

        val subscription = Subscription(priority, handler)
        val node = trie.add(topicPattern, subscription)

        return object : SubscriptionHandle {
            private val active = AtomicBoolean(true)

            override fun unsubscribe() {
                if (active.getAndSet(false)) {
                    node.subscriptions.remove(subscription)
                }
            }
        }
    }

    // ------------------------------------------------------------

    private fun startConsumerIfNeeded(topic: String) {
        consumers.putIfAbsent(
            topic,
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                consumeLoop(topic)
            }
        )
    }

    private suspend fun consumeLoop(topic: String) {
        val queue = eventQueues[topic] ?: return

        while (true) {
            val event = queue.poll() ?: break
            dispatch(event)
        }

        consumers.remove(topic)
    }

    private suspend fun dispatch(event: Event) {
        val matched = trie.match(event.topic).sortedByDescending { it.priority }
        if (matched.isEmpty()) return

        val ctx = EventContext(event)
        for (subscription in matched) {
            if (ctx.isCanceled()) break
            subscription.handler(ctx)
        }
    }

    fun stop() {
        stopped.set(true)
    }

    fun restart() {
        stopped.set(false)
    }

    fun clear() {
        trie.clear()
        eventQueues.clear()
    }
}
