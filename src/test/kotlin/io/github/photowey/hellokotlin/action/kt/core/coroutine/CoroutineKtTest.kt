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

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * {@code CoroutineKtTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/10
 */
object CoroutineKtTest {
    // do nothing now.

    suspend fun showUserInfo() {
        println("Loading user...")
        greet()
        println("User: John Smith")
    }

    suspend fun greet() {
        println("Hello world from a suspending function")
        delay(1_000L)
        println("The greet() on the thread: ${Thread.currentThread().name}")
    }

    suspend fun m1() {
        showUserInfo()
    }

    suspend fun m2() {
        withContext(Dispatchers.Default) {
            this.launch { greet() }
            println("The withContext() on the thread: ${Thread.currentThread().name}")
        }
    }

    suspend fun m3() {
        withContext(Dispatchers.Default) {
            this.launch() {
                greet()
            }

            // Starts another coroutine
            this.launch() {
                println("The CoroutineScope.launch() on the thread: ${Thread.currentThread().name}")
                delay(1.seconds)
                // The delay function simulates a suspending API call here
                // You can add suspending API calls here like a network request
            }

            println("The withContext() on the thread: ${Thread.currentThread().name}")
        }
    }

    suspend fun m4() {
        // Root of the coroutine subtree
        coroutineScope { // this: CoroutineScope
            this.launch {
                println("The coroutineScope-1 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                this.launch {
                    println("The coroutineScope-2 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                    delay(2.seconds)
                    println("Child of the enclosing coroutine completed-${System.currentTimeMillis()}")
                }
                println("Child coroutine 1 completed-${System.currentTimeMillis()}")
            }
            this.launch {
                println("The coroutineScope-3 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                delay(1.seconds)
                println("Child coroutine 2 completed-${System.currentTimeMillis()}")
            }
        }
        // Runs only after all children in the coroutineScope have completed
        println("Coroutine scope completed-${System.currentTimeMillis()}")
    }

    suspend fun m5() {
        val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        singleThreadDispatcher.use { threadDispatcher ->
            coroutineScope {
                withContext(threadDispatcher) {
                    this.launch {
                        println("The coroutineScope-1 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                        this.launch {
                            println("The coroutineScope-2 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                            delay(2.seconds)
                            println("Child of the enclosing coroutine completed-${System.currentTimeMillis()}")
                        }
                        println("Child coroutine 1 completed-${System.currentTimeMillis()}")
                    }
                    this.launch {
                        println("The coroutineScope-3 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                        delay(1.seconds)
                        println("Child coroutine 2 completed-${System.currentTimeMillis()}")
                    }
                }
            }
            println("Coroutine scope completed-${System.currentTimeMillis()}")
        }
    }

    suspend fun m6() {
        coroutineScope {
            withContext(Dispatchers.IO) {
                val dispatcher = currentCoroutineContext()[CoroutineDispatcher]
                println("Current dispatcher: $dispatcher")

                this.launch {
                    println("Dispatcher-1: ${currentCoroutineContext()[CoroutineDispatcher]}-${System.currentTimeMillis()}")

                    println("The coroutineScope-1 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                    this.launch {

                        println("Dispatcher-2: ${currentCoroutineContext()[CoroutineDispatcher]}-${System.currentTimeMillis()}")

                        println("The coroutineScope-2 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                        delay(2.seconds)
                        println("Child of the enclosing coroutine completed-${System.currentTimeMillis()}")
                    }
                    println("Child coroutine 1 completed-${System.currentTimeMillis()}")
                }
                this.launch {
                    println("Dispatcher-3: ${currentCoroutineContext()[CoroutineDispatcher]}-${System.currentTimeMillis()}")

                    println("The coroutineScope-3 on the thread: ${Thread.currentThread().name}-${System.currentTimeMillis()}")
                    delay(1.seconds)
                    println("Child coroutine 2 completed-${System.currentTimeMillis()}")
                }
            }
        }
        println("Coroutine scope completed-${System.currentTimeMillis()}")
    }
}

suspend fun main_001() {
    // CoroutineKtTest.m1()
    // CoroutineKtTest.m2()
    // CoroutineKtTest.m3()
    // CoroutineKtTest.m4()
    // CoroutineKtTest.m5()
    CoroutineKtTest.m6()
}

suspend fun main_002() = withContext(Dispatchers.Default) { // this: CoroutineScope
    // Starts downloading the first page
    val firstPage = this.async {

        delay(50.milliseconds)
        "First page"
    }

    // Starts downloading the second page in parallel
    val secondPage = this.async {
        delay(100.milliseconds)
        "Second page"
    }

    // Awaits both results and compares them
    val pagesAreEqual = firstPage.await() == secondPage.await()
    println("Pages are equal: $pagesAreEqual")
}

suspend fun main() = withContext(Dispatchers.Default) { // this: CoroutineScope
    println("Running withContext block on ${Thread.currentThread().name}")

    val one = this.async {
        println("First calculation starting on ${Thread.currentThread().name}")
        val sum = (1L..500_000L).sum()
        delay(200L)
        println("First calculation done on ${Thread.currentThread().name}")
        sum
    }

    val two = this.async {
        println("Second calculation starting on ${Thread.currentThread().name}")
        val sum = (500_001L..1_000_000L).sum()
        println("Second calculation done on ${Thread.currentThread().name}")
        sum
    }

    // Waits for both calculations and prints the result
    println("Combined total: ${one.await() + two.await()}")
}
