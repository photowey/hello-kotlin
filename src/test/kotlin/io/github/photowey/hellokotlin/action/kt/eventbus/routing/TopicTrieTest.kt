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
package io.github.photowey.hellokotlin.action.kt.eventbus.routing

import io.github.photowey.hellokotlin.action.kt.eventbus.model.EventContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TopicTrieTest {

    private fun dummyHandler(name: String): suspend (EventContext) -> Unit = { }

    @Test
    fun testExactMatch() {
        val trie = TopicTrie()
        val s1 = Subscription(priority = 0, handler = dummyHandler("s1"))
        trie.add("user.login", s1)

        val matched = trie.match("user.login")
        assertEquals(listOf(s1), matched, "Exact match should work")
    }

    @Test
    fun testSingleStarWildcard() {
        val trie = TopicTrie()
        val s1 = Subscription(priority = 0, handler = dummyHandler("star"))
        trie.add("user.*", s1)

        val matched1 = trie.match("user.login")
        val matched2 = trie.match("user.logout")
        val matched3 = trie.match("user.profile.edit")

        assertEquals(listOf(s1), matched1, "* should match one token")
        assertEquals(listOf(s1), matched2, "* should match one token")
        assertEquals(emptyList<Subscription>(), matched3, "* should not match more than one token")
    }

    @Test
    fun testHashWildcard() {
        val trie = TopicTrie()
        val s1 = Subscription(priority = 0, handler = dummyHandler("hash"))
        trie.add("user.#", s1)

        val matched1 = trie.match("user")
        val matched2 = trie.match("user.login")
        val matched3 = trie.match("user.profile.edit")

        assertEquals(listOf(s1), matched1, "# should match zero token")
        assertEquals(listOf(s1), matched2, "# should match one token")
        assertEquals(listOf(s1), matched3, "# should match multiple tokens")
    }

    @Test
    fun testMixedWildcard() {
        val trie = TopicTrie()
        val star = Subscription(priority = 0, handler = dummyHandler("star"))
        val hash = Subscription(priority = 0, handler = dummyHandler("hash"))

        trie.add("user.*.update", star)
        trie.add("user.#.update", hash)

        val matched1 = trie.match("user.profile.update")
        val matched2 = trie.match("user.a.b.c.update")

        println("Matched1: ${matched1.map { it.hashCode() }}")
        println("Matched2: ${matched2.map { it.hashCode() }}")

        assertEquals(listOf(star, hash), matched1, "user.profile.update should match both")
        assertEquals(listOf(hash), matched2, "user.a.b.c.update should match only hash")
    }

    @Test
    fun testHashOnlyPattern() {
        val trie = TopicTrie()
        val hash = Subscription(priority = 0, handler = dummyHandler("hash"))
        trie.add("#", hash)

        val matched1 = trie.match("anything.really.anything")
        val matched2 = trie.match("")

        assertEquals(listOf(hash), matched1, "# should match any topic")
        assertEquals(listOf(hash), matched2, "# should match empty topic")
    }

    @Test
    fun testStarAndHashCombined() {
        val trie = TopicTrie()
        val star = Subscription(priority = 0, handler = dummyHandler("star"))
        val hash = Subscription(priority = 0, handler = dummyHandler("hash"))

        trie.add("a.*.c", star)
        trie.add("a.#.c", hash)

        val matched1 = trie.match("a.b.c")
        val matched2 = trie.match("a.x.y.z.c")

        println("Matched1: ${matched1.map { it.hashCode() }}")
        println("Matched2: ${matched2.map { it.hashCode() }}")

        assertEquals(listOf(star, hash), matched1, "a.b.c should match both")
        assertEquals(listOf(hash), matched2, "a.x.y.z.c should match only hash")
    }
}
