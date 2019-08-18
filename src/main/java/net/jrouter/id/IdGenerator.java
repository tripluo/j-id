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

package net.jrouter.id;

import static net.jrouter.id.impl.IdWorker.DEFAULT_DATACENTERID_BITS;
import static net.jrouter.id.impl.IdWorker.DEFAULT_WORKERID_BITS;

/**
 * Id generator.
 *
 * @param <ID> id type.
 */
@FunctionalInterface
public interface IdGenerator<ID> {

    /**
     * Max 1023 workers[0,1023].
     */
    long MAX_GLOBAL_WORKER_ID = -1L ^ (-1L << (DEFAULT_WORKERID_BITS + DEFAULT_DATACENTERID_BITS));

    /**
     * Generate a new ID.
     *
     * @return the generated ID.
     */
    ID generateId();
}
