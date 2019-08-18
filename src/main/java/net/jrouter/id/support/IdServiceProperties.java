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

import net.jrouter.id.IdGenerator;

/**
 * Properties of IdService.
 */
@lombok.Getter
@lombok.Setter
public class IdServiceProperties {

    /** DISTRIBUTED_ID */
    public static final String DISTRIBUTED_ID = "distributed.id";

    /**
     * 本地文件路径。
     */
    private String localFile = "/" + DISTRIBUTED_ID;

    /**
     * 存储/读取zookeeper的节点路径。
     */
    private String zkPath = DISTRIBUTED_ID;

    /**
     * redis存储的hash键名。
     */
    private String redisHashKey = DISTRIBUTED_ID;

    /**
     * 生成一个代表性的标示字段（如IP）。
     */
    private IdGenerator<String> workerNameGenerator = () -> HostAndPort.NET_ADDRESS;

}
