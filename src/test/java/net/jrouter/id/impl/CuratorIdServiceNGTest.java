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

import net.jrouter.id.support.IdServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * CuratorIdServiceNGTest.
 */
@Slf4j
public class CuratorIdServiceNGTest {

    //local zk address
    public static final String LOCAL_ZK = "127.0.0.1:2181";

    /** CuratorFramework */
    private final CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(LOCAL_ZK)
            .namespace("test")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    private CuratorIdService curatorIdService;

    @BeforeMethod(timeOut = 5000)
    public void setUpMethod() throws Exception {
        assertNotNull(client);
        client.start();
        IdServiceProperties props = new IdServiceProperties();
        curatorIdService = new CuratorIdService(client, props);
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        client.close();
    }

    /**
     * Test of generateId method, of class CuratorIdService.
     */
    @Test
    public void testGenerateId() {
        Long id = curatorIdService.generateId();
        assertNotNull(id);
        log.info("Get id [{}].", id);
    }

}
