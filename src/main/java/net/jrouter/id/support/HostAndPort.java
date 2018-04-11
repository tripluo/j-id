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

import java.io.Serializable;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HostAndPort.
 */
public class HostAndPort implements Serializable {

    /** local host string */
    public static final String LOCALHOST_STRING = getLocalHostQuietly();

    public static boolean isLocalHost(String host) {
        return host.equals("127.0.0.1") || host.startsWith("localhost") || host.equals("0.0.0.0")
                || host.startsWith("169.254")
                || host.startsWith("::1") || host.startsWith("0:0:0:0:0:0:0:1");
    }

    public static String getLocalHostQuietly() {
        String localAddress;
        try {
            localAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            Logger.getLogger(HostAndPort.class.getName()).log(Level.SEVERE, "Can't resolve localhost address.", ex);
            localAddress = "localhost";
        }
        return localAddress;
    }
}
