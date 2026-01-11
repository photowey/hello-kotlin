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
package io.github.photowey.hellokotlin.action.kt.eventbus.util;

import io.github.photowey.hellokotlin.action.kt.eventbus.model.Event

/**
 * {@code TopicValidators}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/11
 */
object EventValidators {
    private val REGEX = Regex("^[a-zA-Z0-9_*#]+(\\.[a-zA-Z0-9_*#]+)*$")
    fun validate(event: Event) {
        require(event.topic.isNotBlank()) { "Event.topic must not be blank" }
        require(event.eventId.isNotBlank()) { "Event.eventId must not be blank" }

        if (!REGEX.matches(event.topic)) {
            throw IllegalArgumentException("Invalid topic: ${event.topic}")
        }
    }

    fun validateTopicPattern(pattern: String) {
        require(pattern.isNotBlank()) { "Topic pattern must not be blank" }

        val tokens = pattern.split(".")
        require(tokens.isNotEmpty()) { "Topic pattern must have at least one token" }

        val regex = Regex("^[a-zA-Z0-9_*#]+$")
        var prevWasWildcard = false

        tokens.forEach { token ->
            require(regex.matches(token)) { "Invalid token '$token' in topic pattern" }

            val isWildcard = token == "*" || token == "#"
            if (isWildcard && prevWasWildcard) {
                throw IllegalArgumentException("Invalid topic pattern: '*' and '#' cannot appear consecutively")
            }

            prevWasWildcard = isWildcard
        }
    }
}
