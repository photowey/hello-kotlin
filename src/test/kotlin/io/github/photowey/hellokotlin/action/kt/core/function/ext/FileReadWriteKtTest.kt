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
package io.github.photowey.hellokotlin.action.kt.core.function.ext

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals

/**
 * {@code FileReadWriteKtTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/10
 */
class FileReadWriteKtTest {

    @Test
    fun testReadTextFromCopiedResource(@TempDir tempDir: File) {
        val input = this::class.java.getResourceAsStream("/dev/txt/hello.txt")
            ?: throw IllegalStateException("Test resource missing")

        val tempFile = File(tempDir, "hello.txt")
        input.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val readed = tempFile.readText(Charsets.UTF_8).trimEnd()
        assertEquals("Hello, Kotlin!", readed)
    }
}
