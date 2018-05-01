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
package net.jrouter.id.support;

import java.nio.file.Files;
import net.jrouter.id.IdGenerator;
import net.jrouter.id.impl.CuratorIdService;
import static net.jrouter.id.impl.CuratorIdServiceNGTest.LOCAL_ZK;
import net.jrouter.id.impl.LocalFileIdService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * CompositeIdGeneratorNGTest
 */
@Slf4j
public class CompositeIdGeneratorNGTest {

    /** CuratorFramework */
    private final CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(LOCAL_ZK)
            .namespace("test")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    private CuratorIdService curatorIdService;

    private LocalFileIdService localFile1;

    private LocalFileIdService localFile2;

    private CompositeIdGenerator<Long> compositeIdGenerator;

    @BeforeMethod(timeOut = 5000)
    public void setUpMethod() throws Exception {
        assertNotNull(client);
        client.start();
        IdServiceProperties props = new IdServiceProperties();
        curatorIdService = new CuratorIdService(client, props);
        localFile1 = new LocalFileIdService(props);

        IdServiceProperties props2 = new IdServiceProperties();
        props2.setLocalFile("test.id.txt");
        localFile2 = new LocalFileIdService(props2);

        //preferred local2->local1->zk
        compositeIdGenerator = new CompositeIdGenerator<>(localFile2, localFile1, curatorIdService);
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        client.close();
        Files.deleteIfExists(localFile2.getLcoalFile());
        Files.deleteIfExists(localFile1.getLcoalFile());
    }

    /**
     * Test of generateId method, of class CompositeIdGenerator.
     */
    @Test
    public void testGenerateId() {
        Long id = compositeIdGenerator.generateId();
        assertNotNull(id);
        assertEquals(id, curatorIdService.generateId());
        assertEquals(id, localFile1.generateId());
        assertEquals(id, localFile2.generateId());

        assertTrue(id <= IdGenerator.MAX_GLOBAL_WORKER_ID);
    }

}
