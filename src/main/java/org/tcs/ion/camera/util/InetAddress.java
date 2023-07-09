package org.tcs.ion.camera.util;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InetAddress {
    private InetAddress() {}

    private static final InetAddressValidator validator = InetAddressValidator.getInstance();

    public static List<String> getInetAddressesInSubnet() {
        List<String> allAddresses = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    java.net.InetAddress inetAddress = interfaceAddress.getAddress();
                    if (networkInterface.isUp() && inetAddress.isSiteLocalAddress()) {
                        System.err.printf("Using network interface %s (%s) with host address %s (%s).%n",
                                networkInterface.getDisplayName(), networkInterface.getName(),
                                inetAddress.getHostName(), inetAddress.getHostAddress());
                        String cidrNotation = inetAddress.getHostAddress() + "/" + interfaceAddress.getNetworkPrefixLength();
                        allAddresses.addAll(Arrays.stream(new SubnetUtils(cidrNotation).getInfo().getAllAddresses()).collect(Collectors.toList()));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return allAddresses;
    }

    public static List<String> inetAddressesBetween(String inetAddress1, String inetAddress2) {
        List<String> ipAddresses = new ArrayList<>();
        try {
            if (!validator.isValidInet4Address(inetAddress1)) {
                throw new Exception(String.format("Invalid IPv4 address '%s'.", inetAddress1));
            }
            if (!validator.isValidInet4Address(inetAddress2)) {
                throw new Exception(String.format("Invalid IPv4 address '%s'.", inetAddress2));
            }

            long ip1 = ip2long(inetAddress1);
            long ip2 = ip2long(inetAddress2);
            if (ip1 > ip2) {
                long ip3 = ip1;
                ip1 = ip2;
                ip2 = ip3;
            }

            for (long ip = ip1; ip <= ip2; ip++)
                ipAddresses.add(long2ip(ip));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return ipAddresses;
    }

    private static long ip2long(String ip) {
        long[] digits = Arrays.stream(ip.split("\\.")).mapToLong(Long::parseLong).toArray();
        return digits[0] << 24 | digits[1] << 16 | digits[2] << 8 | digits[3];
    }

    private static String long2ip(long ip) {
        return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
    }
}
