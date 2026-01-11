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
 * {@code TopicTrie}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/11
 */
internal class TopicTrie {

    private val topicSeparator = '.'
    private val topicStar = "*"
    private val topicHash = "#"

    private val root = TrieNode()

    fun add(pattern: String, subscription: Subscription): TrieNode {
        val tokens = pattern.split('.')
        var parent = root

        for ((i, token) in tokens.withIndex()) {
            val child = when (token) {
                topicStar -> parent.starChild ?: TrieNode().also { parent.starChild = it }
                topicHash -> parent.hashChild ?: TrieNode().also { parent.hashChild = it }
                else -> parent.children.getOrPut(token) { TrieNode() }
            }

            if (token == topicHash || (i == tokens.lastIndex)) {
                val idx = child.subscriptions.indexOfFirst { it.priority < subscription.priority }
                if (idx == -1) child.subscriptions.add(subscription)
                else child.subscriptions.add(idx, subscription)
            }

            parent = child
        }

        /*
        node.subscriptions += subscription
        node.subscriptions.sortByDescending {
            it.priority
        }
        */

        // debugPrint()

        return parent
    }

    fun match(topic: String): List<Subscription> {
        val tokens = topic.split(topicSeparator)
        val result = mutableSetOf<Subscription>()
        dfs(root, tokens, 0, result)
        return result.toList()
    }

    private fun dfs(node: TrieNode, tokens: List<String>, index: Int, result: MutableSet<Subscription>) {
        if (index == tokens.size) {
            result.addAll(node.subscriptions)
        }

        val token = tokens.getOrNull(index)

        token?.let { t ->
            node.children[t]?.let { dfs(it, tokens, index + 1, result) }
        }

        token?.let { _ ->
            node.starChild?.let { dfs(it, tokens, index + 1, result) }
        }

        node.hashChild?.let { hashNode ->
            dfs(hashNode, tokens, index, result)

            for (i in index until tokens.size) {
                dfs(hashNode, tokens, i + 1, result)
            }
        }
    }

    fun clear() {
        root.children.clear()
        root.starChild = null
        root.hashChild = null
        root.subscriptions.clear()
    }

    private fun debugPrint() {
        println("TopicTrie Structure:")
        printNode(root, "")
    }

    private fun printNode(node: TrieNode, prefix: String) {
        if (node.subscriptions.isNotEmpty()) {
            println("$prefix -> subscriptions: ${node.subscriptions.map { it.priority }}")
        }

        node.children.forEach { (key, child) ->
            printNode(child, "$prefix$key.")
        }

        node.starChild?.let {
            printNode(it, "$prefix*.")
        }

        node.hashChild?.let {
            printNode(it, "$prefix#.")
        }
    }
}
