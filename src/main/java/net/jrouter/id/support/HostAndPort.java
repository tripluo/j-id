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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HostAndPort.
 */
public class HostAndPort {

    /** local host address */
    public static final String LOCALHOST_ADDRESS = getLocalHostAddress();

    /** net ip address */
    public static final String NET_ADDRESS = getNetAddress();

    public static boolean isLocalHost(String host) {
        if (host == null || host.isEmpty()) {
            return false;
        }
        return "0.0.0.0".equals(host)
                || "127.0.0.1".equals(host)
                || "localhost".equalsIgnoreCase(host)
                || host.startsWith("169.254")
                || host.startsWith("::1")
                || host.startsWith("0:0:0:0:0:0:0:1");
    }

    public static String getLocalHostAddress() {
        String localAddress;
        try {
            localAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            Logger.getLogger(HostAndPort.class.getName()).log(Level.SEVERE, "Can't resolve localhost address.", ex);
            localAddress = "localhost";
        }
        return localAddress;
    }

    public static String getNetAddress() {
        InetAddress address = getInetAddresses();
        if (address != null) {
            return address.getHostAddress();
        }
        return getLocalHostAddress();
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String host = address.getHostAddress();
        if (host == null || host.isEmpty()) {
            return false;
        }
        return !isLocalHost(host);
    }

    public static InetAddress getInetAddresses() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {//NOPMD AvoidCatchingThrowable
                                //ignore
                            }
                        }
                    } catch (Throwable e) {//NOPMD AvoidCatchingThrowable
                        //ignore
                    }
                }
            }
        } catch (SocketException e) {
            //ignore
        }
        return null;
    }
}
