package org.tcs.ion.camera;

import be.teletask.onvif.DiscoveryManager;
import be.teletask.onvif.listeners.DiscoveryListener;
import be.teletask.onvif.models.Device;
import org.tcs.ion.camera.util.Logger;

import java.util.List;

public class OnvifDeviceDiscovery {
    int timeoutInSeconds = 3;
    DiscoveryManager manager;

    public OnvifDeviceDiscovery() {
        manager = new DiscoveryManager();
        manager.setDiscoveryTimeout(timeoutInSeconds * 1000);
    }

    public int discover(boolean authRequire) {
        final Integer[] count = {0};
        manager.discover(new DiscoveryListener() {
            @Override
            public void onDiscoveryStarted() {
                Logger.log("ONVIF DEVICE DISCOVERY STARTED.");
            }

            @Override
            public void onDevicesFound(List<Device> devices) {
                Logger.log("ONVIF DEVICE DISCOVERY ENDED.");

                if (!devices.isEmpty())
                    new OnvifDeviceEnquiry(authRequire).inquireByDevice(devices);

                synchronized (count[0]) {
                    count[0] = devices.size();
                    count[0].notify();
                }
            }
        });

        try {
            Logger.log("ONVIF DEVICE DISCOVERY WAITING.");
            synchronized (count[0]) {
                count[0].wait();
            }
        } catch (InterruptedException e) {
            Logger.log(e);
        }
        return count[0];
    }
}
