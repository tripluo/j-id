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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.jrouter.id.IdGenerator;

/**
 * Composite IdService, preferred first non-null ID generator and store ID data back to null ID generators.
 *
 * @param <ID> id type.
 */
@Slf4j
public class CompositeIdGenerator<ID> implements IdGenerator<ID> {

    @lombok.Getter
    @lombok.Setter
    private List<IdGenerator<ID>> idGenerators;

    /**
     * Construct a CompositeIdGenerator from the given delegate IdGenerators.
     *
     * @param generators the IdGenerators to delegate to.
     */
    public CompositeIdGenerator(IdGenerator<ID>... generators) {
        this(Arrays.asList(generators));
    }

    /**
     * Construct a CompositeIdGenerator from the given delegate IdGenerators.
     *
     * @param idGenerators the IdGenerators to delegate to.
     */
    public CompositeIdGenerator(List<IdGenerator<ID>> idGenerators) {
        this.idGenerators = idGenerators;
    }

    @Override
    public ID generateId() {
        ID id = null;
        List<IdStorager<ID>> storagers = new ArrayList<>(idGenerators.size() >> 1);
        for (IdGenerator<ID> gen : idGenerators) {
            id = gen.generateId();
            if (id == null) {
                //check if IdStorager
                if (gen instanceof IdStorager) {
                    storagers.add((IdStorager) gen);
                }
            } else {
                break;
            }
        }
        //feed back data storager
        if (id != null && !storagers.isEmpty()) {
            feedback(storagers, id);
        }
        return id;
    }

    /**
     * Store ID value.
     */
    private void feedback(List<IdStorager<ID>> storagers, ID id) {
        storagers.forEach((storager) -> {
            try {
                storager.storeId(id);
            } catch (Exception e) {
                log.error("Store {} in {} fail.", id, storager, e);
            }
        });
    }
}
