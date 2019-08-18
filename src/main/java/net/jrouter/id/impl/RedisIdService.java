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

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import net.jrouter.id.IdGenerator;
import net.jrouter.id.support.IdServiceProperties;
import net.jrouter.id.support.IdStorager;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Use Redis hashes to generate and store Id.
 */
@Slf4j
public class RedisIdService implements IdGenerator<Long>, IdStorager<Long> {

    /** BoundHashOperations */
    private final BoundHashOperations boundHashOperations;

    /** Properties */
    private final IdServiceProperties properties;

    @lombok.Setter
    private String countKey = "count";

    /**
     * Constructor.
     *
     * @param redisTemplate RedisTemplate
     * @param properties IdServiceProperties
     */
    public RedisIdService(RedisTemplate redisTemplate, IdServiceProperties properties) {
        Objects.requireNonNull(redisTemplate);
        Objects.requireNonNull(properties);
        this.properties = properties;
        this.boundHashOperations = redisTemplate.boundHashOps(properties.getRedisHashKey());
    }

    @Override
    public Long generateId() {
        String key = properties.getWorkerNameGenerator().generateId();
        Long id = getLong(key);
        out:
        if (id == null || id < 1) {
            //get false if already has key
            if (boundHashOperations.putIfAbsent(key, 0)) {
                id = boundHashOperations.increment(countKey, 1);
                boundHashOperations.put(key, id);
            } else {
                //try 3 times
                for (int i = 0; i < 3; i++) {
                    id = getLong(key);
                    if (id == null || id < 1) {
                        try {
                            //sleep for a while
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                    } else {
                        break out;
                    }
                }
                throw new IllegalArgumentException(String.format("Can't get right number from worker name key [%s]", key));
            }
        }
        return id;
    }

    /**
     * Get {@code Long} value from key.
     *
     * @param key {@code String} type key.
     *
     * @return {@code Long} value.
     */
    private Long getLong(String key) {
        Object obj = boundHashOperations.get(key);
        if (obj != null) {
            if (obj instanceof Number) {
                return ((Number) obj).longValue();
            }
            if (obj instanceof String) {
                return Long.parseLong((String) obj);
            }
        }
        return null;
    }

    @Override
    public void storeId(Long id) {
        boundHashOperations.putIfAbsent(properties.getWorkerNameGenerator().generateId(), id);
    }
}
