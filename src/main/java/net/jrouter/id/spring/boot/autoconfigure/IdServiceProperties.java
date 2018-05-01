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

import net.jrouter.id.IdGenerator;
import static net.jrouter.id.support.IdServiceProperties.DISTRIBUTED_ID;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties of IdService.
 */
@ConfigurationProperties(prefix = DISTRIBUTED_ID)
@lombok.Getter
@lombok.Setter
public class IdServiceProperties extends net.jrouter.id.support.IdServiceProperties {

    /**
     * Generator Type. Using redis by default.
     */
    private GeneratorType generatorType = GeneratorType.REDIS;

    /**
     * 是否存储本地文件。
     */
    private boolean enableLocalFileStorager = true;

    /**
     * Manual write the worker id.
     *
     * @see GeneratorType#MANUAL
     * @see IdGenerator#MAX_GLOBAL_WORKER_ID
     */
    private long manualWorkerId = 0;

}
