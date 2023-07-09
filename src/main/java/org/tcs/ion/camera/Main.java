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
        options.addOption(Option.builder("DISCOVERY").option("d").longOpt("discovery").desc("Multicast discovery to locate services on local network.").build());
        options.addOption(Option.builder("SUBNET").option("s").longOpt("subnet").desc("All IP addresses on local network(s).").build());
        options.addOption(Option.builder("HOSTS").option("n").longOpt("hostnames").desc("Hostname(s) and/or IP address(es).").hasArgs().build());
        options.addOption(Option.builder("RANGE").option("r").longOpt("range").desc("All IP addresses between two IP addresses.").hasArgs().numberOfArgs(2).build());
        options.addOption(Option.builder("FILE").option("f").longOpt("file").desc("Devices from CSV or Excel file (Headers: hostname, username, password)").hasArg().build());
        options.addOption(Option.builder("AUTH").option("a").longOpt("with-auth").desc("Indicate services require authentication. Authenticate each host individually not supported yet.").build());
        options.addOption(Option.builder("HELP").option("h").longOpt("help").desc("Display this help text.").build());

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
            System.out.print("Try this ");
            printHelp();
        } else {
            List<String> hostnames = new ArrayList<>();

            if (line.hasOption('d'))
                hostnames.addAll(new OnvifDeviceDiscovery().discover());

            if (line.hasOption('s'))
                hostnames.addAll(InetAddress.getInetAddressesInSubnet());

            if (line.hasOption('n'))
                hostnames.addAll(Arrays.stream(line.getOptionValues('n'))
                        .flatMap(a -> Arrays.stream(a.split(","))).collect(Collectors.toList()));

            if (line.hasOption('r')) {
                String[] ips = line.getOptionValues('r');
                hostnames.addAll(InetAddress.inetAddressesBetween(ips[0], ips[1]));
            }

            if (line.hasOption('f')) {
                line.getOptionValue('f');
            }

            int count = new OnvifDeviceEnquiry(line.hasOption('a')).inquireByHostname(hostnames);
            if (count > 0) {
                Logger.msg(count + " ONVIF device(s) reachable out of " + hostnames.size() + " host(s).");
            } else {
                Logger.msg("No ONVIF device(s) are reachable.");
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
