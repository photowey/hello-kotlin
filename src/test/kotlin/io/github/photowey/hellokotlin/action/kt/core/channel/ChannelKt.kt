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
package io.github.photowey.hellokotlin.action.kt.core.channel;

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * {@code ChannelKt}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/11
 */
class ChannelKt {
    // do nothing now.
}

fun main_01(args: Array<String>) {
    return runBlocking {
        val channel = Channel<Int>()

        this.launch {
            get(channel)
        }

        this.launch {
            set(channel)
        }
    }
}

fun main_02() = runBlocking {
    val flow = helloProducer()
    receiver(flow)
}

fun main() = runBlocking {
    val channel = helloProducerChannel()
    receiver(channel)
    delay(1_000L)
}

// ----------------------------------------------------------------

fun helloProducer(): Flow<Int> = channelFlow {
    withContext(Dispatchers.Default) {
        for (i in 1..5) {
            delay(100)
            send(i)
        }
    }
}

suspend fun receiver(flow: Flow<Int>) {
    flow.collect { value ->
        println("Received: $value")
    }
}

// ----------------------------------------------------------------

fun CoroutineScope.helloProducerChannel(): ReceiveChannel<Int> = produce {
    withContext(Dispatchers.Default) {
        for (i in 1..5) {
            delay(100)
            send(i)
        }
    }
}

fun CoroutineScope.receiver(channel: ReceiveChannel<Int>) {
    launch(Dispatchers.Default) {
        for (value in channel) {
            println("Received: $value")
        }
    }
}

// ----------------------------------------------------------------

suspend fun get(channel: Channel<Int>) {
    var counter = 0
    while (true) {
        val received = channel.receive()
        println("Received: $counter->$received")
        counter++

        if (counter == 100) {
            break
        }
    }
}

suspend fun set(channel: Channel<Int>) {
    var i = 0

    while (true) {
        channel.send(i++)
        if (i == 100) {
            break
        }
    }
}
