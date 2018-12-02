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

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.jrouter.id.IdGenerator;
import net.jrouter.id.impl.IdGenerator2018;
import net.jrouter.id.impl.CuratorIdService;
import net.jrouter.id.impl.LocalFileIdService;
import net.jrouter.id.impl.RedisIdService;
import static net.jrouter.id.spring.boot.autoconfigure.GeneratorType.*;
import net.jrouter.id.support.CompositeIdGenerator;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

/**
 * IdServiceAutoConfiguration.
 */
@Configuration
@EnableConfigurationProperties(IdServiceProperties.class)
@Slf4j
public class IdServiceAutoConfiguration {

    private final IdServiceProperties idServiceProperties;

    public IdServiceAutoConfiguration(IdServiceProperties idServiceProperties) {
        this.idServiceProperties = idServiceProperties;
    }

    @Configuration
    @ConditionalOnProperty(prefix = IdServiceProperties.DISTRIBUTED_ID, name = "generatorType", havingValue = "redis",
            matchIfMissing = true)
    static class RedisWorkerIdConfiguration {

        @Autowired
        private IdServiceProperties idServiceProperties;

        /**
         * Build a RedisTemplate with {@code String} type hash key and {@code Long} type hash value.
         *
         * @param redisConnectionFactory {@code RedisConnectionFactory}
         *
         * @return the RedisTemplate
         *
         * @see StringRedisTemplate
         */
        @Bean
        @ConditionalOnMissingBean(name = "idServiceRedisTemplate")
        public RedisTemplate<String, Long> idServiceRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Long> template = new RedisTemplate<>();
            RedisSerializer<String> stringSerializer = new StringRedisSerializer();
            template.setDefaultSerializer(stringSerializer);
            template.setKeySerializer(stringSerializer);
            template.setValueSerializer(stringSerializer);
            template.setHashKeySerializer(stringSerializer);
            template.setHashValueSerializer(new GenericToStringSerializer(Long.class));
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        @ConditionalOnMissingBean(name = "redisWorkerIdGenerator")
        IdGenerator<Long> workerIdGenerator(RedisTemplate<String, Long> idServiceRedisTemplate) {
            return new RedisIdService(idServiceRedisTemplate, idServiceProperties);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = IdServiceProperties.DISTRIBUTED_ID, name = "generatorType",
            havingValue = "zookeeper")
    static class ZkWorkerIdConfiguration {

        @Autowired
        private IdServiceProperties idServiceProperties;

        @Autowired
        private CuratorFramework curatorFramework;

        @Bean
        @ConditionalOnMissingBean(name = "zkWorkerIdGenerator")
        IdGenerator<Long> workerIdGenerator() {
            return new CuratorIdService(curatorFramework, idServiceProperties);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = IdServiceProperties.DISTRIBUTED_ID, name = "generatorType",
            havingValue = "local")
    static class LocalWorkerIdConfiguration {

        @Autowired
        private IdServiceProperties idServiceProperties;

        @Bean
        @ConditionalOnMissingBean(name = "localWorkerIdGenerator")
        IdGenerator<Long> workerIdGenerator() {
            return new LocalFileIdService(idServiceProperties);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = IdServiceProperties.DISTRIBUTED_ID, name = "generatorType",
            havingValue = "manual")
    static class ManualWorkerIdConfiguration {

        @Autowired
        private IdServiceProperties idServiceProperties;

        @Bean
        @ConditionalOnMissingBean(name = "manualWorkerIdGenerator")
        IdGenerator<Long> workerIdGenerator() {
            return () -> idServiceProperties.getManualWorkerId();
        }
    }

    @Bean
    IdGenerator<Long> idGenerator(IdGenerator<Long> workerIdGenerator) {
        Long workerId = null;
        switch (idServiceProperties.getGeneratorType()) {
            case LOCAL:
            case MANUAL: {
                workerId = workerIdGenerator.generateId();
                break;
            }
            default: {
                List<IdGenerator<Long>> workerIdGenerators = new ArrayList<>(2);
                if (idServiceProperties.isEnableLocalFileStorager()) {
                    //add local file file
                    workerIdGenerators.add(new LocalFileIdService(idServiceProperties));
                }
                workerIdGenerators.add(workerIdGenerator);
                workerId = new CompositeIdGenerator<>(workerIdGenerators).generateId();
                break;
            }
        }
        Assert.notNull(workerId, String.format("Can't generate workerId from [%s]", workerIdGenerator.toString()));
        IdGenerator<Long> idGen = new IdGenerator2018(workerId);
        if (log.isDebugEnabled()) {
            log.debug("Use workerId [{}] for id generator [{}]", workerId, idGen);
        }
        return idGen;
    }

}
