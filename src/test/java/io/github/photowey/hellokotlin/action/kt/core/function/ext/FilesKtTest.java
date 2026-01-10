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
package io.github.photowey.hellokotlin.action.kt.core.function.ext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kotlin.io.FilesKt;
import kotlin.text.Charsets;

/**
 * {@code FilesKtTest}.
 *
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/10
 */
class FilesKtTest {

    @Test
    void testReadText(@TempDir Path tempDir) throws IOException {
        try (InputStream input = Optional.ofNullable(
                this.getClass().getResourceAsStream("/dev/txt/hello.txt"))
            .orElseThrow(() -> {
                return new FileNotFoundException("Test resource missing");
            })
        ) {
            File file = tempDir.resolve("hello.txt").toFile();
            input.transferTo(new FileOutputStream(file));

            String readed = FilesKt.readText(file, Charsets.UTF_8).stripTrailing();
            Assertions.assertEquals("Hello, Kotlin!", readed);
        }
    }
}
