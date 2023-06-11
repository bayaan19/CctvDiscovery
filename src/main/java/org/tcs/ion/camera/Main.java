package org.tcs.ion.camera;

import org.apache.commons.cli.*;
import org.tcs.ion.camera.util.InetAddress;
import org.tcs.ion.camera.util.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final Options options = new Options();

    public static void main(String[] args) {
        options.addOption(Option.builder("SUBNET").option("s").longOpt("subnet").desc("all IP addresses in connected subnetwork, suppresses -a, -r options").build());
        options.addOption(Option.builder("HOSTS").option("a").longOpt("addresses").desc("hostnames and/or ip addresses").hasArgs().build());
        options.addOption(Option.builder("RANGE").option("r").longOpt("range").desc("all IP addresses between two IP addresses").hasArgs().numberOfArgs(2).build());

        try {
            CommandLine line = new DefaultParser().parse(options, args);
            workflow(line);
        } catch (ParseException e) {
            Logger.log(e);
            printHelp();
        }
    }

    private static void workflow(CommandLine line) {
        if (line.getOptions().length == 0) {
            int count = new OnvifDeviceDiscovery().discover();
            if (count > 0) {
                Logger.msg(count + " ONVIF device(s) discovered using WS-Discovery.");
            } else {
                Logger.msg("ONVIF device discovery unsuccessful using WS-Discovery.");
                System.out.print("Try this ");
                printHelp();
            }
        } else {
            if (line.hasOption("s")) {
                new OnvifDeviceEnquiry().inquireByHostname(InetAddress.getInetAddressesInSubnet());
            } else {
                List<String> addresses = new ArrayList<>();
                if (line.hasOption("a")) {
                    addresses.addAll(Arrays.stream(line.getOptionValues('a')).flatMap(a -> Arrays.stream(a.split(","))).collect(Collectors.toList()));
                }

                if (line.hasOption("r")) {
                    String[] ips = line.getOptionValues('r');
                    addresses.addAll(InetAddress.inetAddressesBetween(ips[0], ips[1]));
                }

                int count = new OnvifDeviceEnquiry().inquireByHostname(addresses);
                if (count > 0) {
                    Logger.msg(count + " ONVIF device(s) reachable out of " + addresses.size() + " host(s).");
                } else {
                    Logger.msg("No ONVIF device(s) are reachable.");
                }
            }
        }
    }

    private static void printHelp() {
        try {
            new HelpFormatter().printHelp("java -jar " + new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getName(), options, true);
        } catch (URISyntaxException e) {
            Logger.log(e);
        }
    }
}
