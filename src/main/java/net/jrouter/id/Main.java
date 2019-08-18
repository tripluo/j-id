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

import java.util.Date;
import java.util.logging.Logger;
import net.jrouter.id.impl.IdGenerator2018;
import net.jrouter.id.support.HostAndPort;

/**
 * Main.
 */
public class Main { //NOPMD

    /** base package */
    public static final String BASE_PACKAGE = "net.jrouter.id";

    /** version */
    public static final String VERSION = "1.4";

    //log
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    //main
    public static void main(String[] args) {
        System.out.println(String.format("%s: %s", BASE_PACKAGE, VERSION));//NOPMD
        System.out.println("localhost: " + HostAndPort.LOCALHOST_ADDRESS);//NOPMD
        System.out.println("net address: " + HostAndPort.NET_ADDRESS);//NOPMD
        System.out.println("Usage: [long]");//NOPMD
        if (args.length > 0) {
            long id = Long.parseLong(args[0]);
            IdGenerator2018 id2018 = new IdGenerator2018(0);
            System.out.println("--------------------------------------------------------------------------------");//NOPMD
            System.out.println(String.format("Parse id: %d, using %s", id, id2018.getClass().getName()));//NOPMD
            long minId = id2018.minId();
            System.out.println(String.format("Min id: %d, %s", minId, new Date(id2018.parseTimeMillis(minId))));//NOPMD
            long timeMillis = id2018.parseTimeMillis(id);
            if (id < minId) {
                LOG.severe(String.format("Id generation error, must be greater than %d", minId));
            }
            System.out.println(String.format("TimeMillis: %d, %s", timeMillis, new Date(timeMillis)));//NOPMD
            System.out.println("GlobalWorkerId: " + id2018.parseGlobalWorkerId(id));//NOPMD
            System.out.println("DatacenterId: " + id2018.parseDatacenterId(id));//NOPMD
            System.out.println("WorkerId: " + id2018.parseWorkerId(id));//NOPMD
            System.out.println("Sequence: " + id2018.parseSequence(id));//NOPMD
        }
    }
}
