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

import java.nio.file.Files;
import net.jrouter.id.support.IdServiceProperties;
import lombok.extern.slf4j.Slf4j;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * LocalFileIdServiceNGTest.
 */
@Slf4j
public class LocalFileIdServiceNGTest {

    private LocalFileIdService localFileIdServicel;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        IdServiceProperties props = new IdServiceProperties();
        props.setLocalFile("/distributed.Id");
        localFileIdServicel = new LocalFileIdService(props);
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        Files.deleteIfExists(localFileIdServicel.getLcoalFile());
    }

    /**
     * Test of generateId method, of class LocalFileIdService.
     */
    @Test
    public void testGenerateId() {
        log.info("Local file {}", localFileIdServicel.getLcoalFile().toAbsolutePath());
        assertEquals(null, localFileIdServicel.generateId());

    }

    /**
     * Test of storeId method, of class LocalFileIdService.
     */
    @Test
    public void testStoreId() throws Exception {
        Long id = 1234567890L;
        localFileIdServicel.storeId(id);
        assertEquals(id, localFileIdServicel.generateId());
    }

}
