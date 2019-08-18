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

import lombok.extern.slf4j.Slf4j;
import net.jrouter.id.Main;
import net.jrouter.id.spring.boot.autoconfigure.IdServiceAutoConfiguration;
import net.jrouter.id.support.IdServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * RedisIdServiceNGTest.
 */
@SpringBootTest
@Slf4j
public class RedisIdServiceNGTest extends AbstractTestNGSpringContextTests {

    /**
     * 排除 IdServiceAutoConfiguration 自定义Bean测试。
     *
     * @see IdServiceAutoConfiguration
     */
    @SpringBootApplication(scanBasePackages = Main.BASE_PACKAGE, exclude = IdServiceAutoConfiguration.class)
    @ComponentScan
    static class Config {

        @Autowired
        private RedisConnectionFactory redisConnectionFactory;

        /**
         * Set {@code String} type hash key and {@code Long} type hash value.
         *
         * @see StringRedisTemplate
         */
        public static class IdServiceRedisTemplate extends RedisTemplate<String, Long> {

            public IdServiceRedisTemplate() {
                RedisSerializer<String> stringSerializer = new StringRedisSerializer();
                setDefaultSerializer(stringSerializer);
                setKeySerializer(stringSerializer);
                setValueSerializer(stringSerializer);
                setHashKeySerializer(stringSerializer);
                setHashValueSerializer(new GenericToStringSerializer(Long.class));
            }
        }

        @Bean
        IdServiceRedisTemplate idServiceRedisTemplate() {
            IdServiceRedisTemplate template = new IdServiceRedisTemplate();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        RedisIdService redisIdService(IdServiceRedisTemplate template) {
            RedisIdService redisIdService = new RedisIdService(template, idServiceProperties());
            return redisIdService;
        }

        @Bean
        IdServiceProperties idServiceProperties() {
            IdServiceProperties props = new IdServiceProperties();
            return props;
        }

    }

    @Autowired
    private RedisIdService redisKeyGenerator;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        assertNotNull(redisKeyGenerator);
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of generateId method, of class RedisIdService.
     */
    @Test
    public void testGenerateId() {
        Long id = redisKeyGenerator.generateId();
        assertNotNull(id);
        log.info("Get id [{}].", id);
    }

}
