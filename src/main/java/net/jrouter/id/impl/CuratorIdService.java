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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import net.jrouter.id.IdGenerator;
import net.jrouter.id.support.IdServiceProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

/**
 * Generate Id using {@code CuratorFramework}.
 */
@Slf4j
public class CuratorIdService implements IdGenerator<Long> {

    /** CuratorFramework */
    private final CuratorFramework curatorFramework;

    /** Properties */
    private final IdServiceProperties properties;

    @lombok.Setter
    private Charset charset = StandardCharsets.UTF_8;

    @lombok.Setter
    private String nodeName = "id";

    /**
     * Sequence Node's number (%010d -- that is 10 digits with 0 (zero) padding).
     */
    private static final int SEQUENTIAL_NUMBER_LENGTH = 10;

    /**
     * Constructor.
     *
     * @param curatorFramework CuratorFramework
     * @param properties IdServiceProperties
     */
    public CuratorIdService(CuratorFramework curatorFramework, IdServiceProperties properties) {
        Objects.requireNonNull(curatorFramework);
        Objects.requireNonNull(properties);
        this.curatorFramework = curatorFramework;
        this.properties = properties;
    }

    @Override
    public Long generateId() {
        String parent = ZKPaths.makePath(ZKPaths.PATH_SEPARATOR, properties.getZkPath());
        String seqNodePath = ZKPaths.makePath(parent, nodeName);
        String nodeData = properties.getWorkerNameGenerator().generateId();
        String nodePath = ZKPaths.makePath(parent, nodeData);
        try {
            curatorFramework.checkExists().creatingParentsIfNeeded().forPath(seqNodePath);
            String val = getMatchedChildrenNode(parent, nodeData);
            out:
            if (val == null || val.length() < SEQUENTIAL_NUMBER_LENGTH) {
                //multi-create conflict may throws NodeExistsException
                try {
                    curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(nodePath, new byte[0]);
                } catch (KeeperException.NodeExistsException e) {
                    //try 3 times
                    for (int i = 0; i < 3; i++) {
                        val = getMatchedChildrenNode(parent, nodeData);
                        if (val == null || val.length() < SEQUENTIAL_NUMBER_LENGTH) {
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
                    throw new IllegalArgumentException(String.format("Can't get right node from worker name data [%s]", nodeData), e);
                }
                val = curatorFramework.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(seqNodePath, nodeData.getBytes(charset));
                //set for view if curator client is not closed
                curatorFramework.setData().forPath(nodePath, val.getBytes(charset));
            }
            return parseSequentialNode(val);
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    /**
     * Parse Sequence Node's number (%010d -- that is 10 digits with 0 (zero) padding).
     *
     * @param seqNode Sequence Node's name.
     *
     * @return {@code Long} number.
     */
    private static Long parseSequentialNode(String seqNode) {
        int len = 0;
        if (seqNode == null || (len = seqNode.length()) < SEQUENTIAL_NUMBER_LENGTH) {
            throw new IllegalStateException(String.format("%s is not a valid sequential value.", seqNode));
        }
        return Long.parseLong(seqNode.substring(len - SEQUENTIAL_NUMBER_LENGTH));
    }

    /**
     * Get data.
     */
    private String getData(String path) {
        try {
            return new String(curatorFramework.getData().forPath(path), charset);
        } catch (KeeperException.NoNodeException ex) {
            //ignore
            return null;
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    /**
     * Find first children node name with matched data.
     */
    private String getMatchedChildrenNode(String path, String data) throws Exception { //NOPMD SignatureDeclareThrowsException
        return curatorFramework.getChildren().forPath(path).stream()
                .filter(v -> data.equals(getData(ZKPaths.makePath(path, v))))
                .findFirst()
                .orElse(null);
    }
}
