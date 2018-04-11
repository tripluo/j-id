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

import net.jrouter.id.IdGenerator;

/**
 * Id Generator with initial epoch 20180101.
 */
public class IdGenerator2018 extends IdWorker implements IdGenerator<Long> {

    //20180101 00:00:00
    private final long d20180101 = 1514736000000L;

    public IdGenerator2018(long globalWorkerId) {
        super(globalWorkerId);
    }

    public IdGenerator2018(long workerId, long datacenterId) {
        super(workerId, datacenterId);
    }

    @Override
    public long initialTimeMillis() {
        return d20180101;
    }

    @Override
    public Long generateId() {
        return nextId();
    }
}
