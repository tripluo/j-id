/*
 * Copyright (C) 2010-2111 sunjumper@163.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.jrouter.id.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import net.jrouter.id.IdGenerator;
import net.jrouter.id.support.IdServiceProperties;
import net.jrouter.id.support.IdStorager;
import lombok.extern.slf4j.Slf4j;

/**
 * Use local file to store and get Id.
 */
@Slf4j
public class LocalFileIdService implements IdGenerator<Long>, IdStorager<Long> {

    /** local file */
    @lombok.Getter
    private final Path lcoalFile;

    /**
     * Constructor.
     *
     * @param properties IdServiceProperties
     */
    public LocalFileIdService(IdServiceProperties properties) {
        Objects.requireNonNull(properties);
        lcoalFile = Paths.get(properties.getLocalFile());
        if (Files.exists(lcoalFile, LinkOption.NOFOLLOW_LINKS)) {
            if (!Files.isRegularFile(lcoalFile) || !Files.isReadable(lcoalFile)) {
                throw new IllegalStateException(String.format("%s is not a regular file or not readable.", lcoalFile));
            }
        }
    }

    @Override
    public Long generateId() {
        try {
            if (Files.isReadable(lcoalFile) && Files.size(lcoalFile) > 0) {
                return Long.parseLong(new String(Files.readAllBytes(lcoalFile), StandardCharsets.UTF_8));
            }
        } catch (IOException | NumberFormatException ex) {
            log.error("Generate Id from {} error.", lcoalFile, ex);
        }
        return null;
    }

    @Override
    public void storeId(Long id) {
        try {
            Files.write(lcoalFile, String.valueOf(id).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public String toString() {
        return "LocalFileWorkerIdService{" + "lcoalFile=" + lcoalFile + '}';
    }

}
