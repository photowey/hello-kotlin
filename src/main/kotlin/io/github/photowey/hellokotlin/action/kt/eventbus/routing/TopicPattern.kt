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
package io.github.photowey.hellokotlin.action.kt.eventbus.routing;

/**
 * {@code TopicPattern}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/11
 */
internal class TopicPattern(private val pattern: String) {

    private val parts = pattern.split('.')

    fun matches(topic: String): Boolean {
        val topicParts = topic.split('.')
        return match(parts, topicParts)
    }

    private fun match(pattern: List<String>, topic: List<String>): Boolean {
        var p = 0
        var t = 0

        while (p < pattern.size && t < topic.size) {
            when (val token = pattern[p]) {
                "#" -> {
                    return true
                }

                "*" -> {
                    p++
                    t++
                }

                else -> {
                    if (token != topic[t]) return false
                    p++
                    t++
                }
            }
        }

        return when (p) {
            pattern.size if t == topic.size -> true
            pattern.size - 1 if pattern[p] == "#" -> true
            else -> false
        }
    }
}
