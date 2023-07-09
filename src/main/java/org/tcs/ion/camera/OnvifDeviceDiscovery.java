package org.tcs.ion.camera;

import be.teletask.onvif.DiscoveryManager;
import be.teletask.onvif.listeners.DiscoveryListener;
import be.teletask.onvif.models.Device;
import org.tcs.ion.camera.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OnvifDeviceDiscovery {
    int timeoutInSeconds = 60;
    DiscoveryManager manager;

    public OnvifDeviceDiscovery() {
        manager = new DiscoveryManager();
        manager.setDiscoveryTimeout(timeoutInSeconds * 1000);
    }

    public List<String> discover() {
        final List<String>[] hostnames = new List[]{new ArrayList<>()};

        manager.discover(new DiscoveryListener() {
            @Override
            public void onDiscoveryStarted() {
                Logger.log("ONVIF DEVICE DISCOVERY STARTED.");
            }

            @Override
            public void onDevicesFound(List<Device> devices) {
                Logger.log("ONVIF DEVICE DISCOVERY ENDED.");

                synchronized (hostnames[0]) {
                    if (!devices.isEmpty())
                        hostnames[0].addAll(devices.stream().map(Device::getHostName).collect(Collectors.toList()));
                    hostnames[0].notify();
                }
            }
        });

        try {
            Logger.log("ONVIF DEVICE DISCOVERY WAITING.");
            synchronized (hostnames[0]) {
                hostnames[0].wait();
            }
        } catch (InterruptedException e) {
            Logger.log(e);
        }

        int count = hostnames[0].size();
        if (count > 0) {
            Logger.msg(count + " ONVIF device(s) discovered using WS-Discovery.");
        } else {
            Logger.msg("ONVIF device discovery unsuccessful using WS-Discovery.");
        }

        return hostnames[0];
    }
}
