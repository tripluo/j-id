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

import lombok.extern.slf4j.Slf4j;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * HostAndPortNGTest.
 */
@Slf4j
public class HostAndPortNGTest {

    /**
     * Test of getLocalHostAddress method, of class HostAndPort.
     */
    @Test
    public void testGetLocalHostQuietly() {
        log.info("Get LOCALHOST_ADDRESS : {}", HostAndPort.LOCALHOST_ADDRESS);
        log.info("Get NET_ADDRESS : {}", HostAndPort.NET_ADDRESS);
    }

    /**
     * Test of isLocalHost method, of class HostAndPort.
     */
    @Test
    public void testIsLocalHost() {
        assertTrue(HostAndPort.isLocalHost("0.0.0.0"));
        assertTrue(HostAndPort.isLocalHost("127.0.0.1"));
        assertTrue(HostAndPort.isLocalHost("localhost"));
        assertTrue(HostAndPort.isLocalHost("169.254.0.0"));
        assertFalse(HostAndPort.isLocalHost("192.168.0.123"));
    }

}
