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

package net.jrouter.id.spring.boot.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import net.jrouter.id.IdGenerator;
import net.jrouter.id.Main;
import net.jrouter.id.impl.IdGenerator2018;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * IdServiceAutoConfigurationNGTest.
 *
 * @see IdServiceAutoConfiguration
 */
@SpringBootTest
@Slf4j
public class IdServiceAutoConfigurationNGTest extends AbstractTestNGSpringContextTests {

    @SpringBootApplication(scanBasePackages = Main.BASE_PACKAGE)
    @ComponentScan
    static class Config {

    }

    @Autowired
    private IdServiceProperties idServiceProperties;

    @Autowired
    private IdGenerator<Long> idGenerator;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        assertNotNull(idServiceProperties);
        assertNotNull(idGenerator);
        assertEquals(idGenerator.getClass(), IdGenerator2018.class);
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of idServiceRedisTemplate method, of class IdServiceAutoConfiguration.
     */
    @Test
    public void testIdServiceRedisTemplate() {
        Long id = idGenerator.generateId();
        assertNotNull(id);
        log.info("Get id [{}].", id);
    }

}
