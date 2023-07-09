package org.tcs.ion.camera;

import be.teletask.onvif.OnvifManager;
import be.teletask.onvif.models.OnvifDevice;
import org.tcs.ion.camera.models.OnvifDevicesData;
import org.tcs.ion.camera.util.Input;
import org.tcs.ion.camera.util.Logger;
import org.tcs.ion.camera.util.Read;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OnvifDeviceEnquiry {
    private final boolean authRequire;
    int awaitTimeoutInMinute = 1;
    OnvifDevicesData data;
    private Input.UserPass authDetails;

    public OnvifDeviceEnquiry(boolean authRequire) {
        this.data = new OnvifDevicesData();
        this.authRequire = authRequire;
        if (authRequire)
            this.authDetails = Input.getUserPass();
    }

    private void getServicesInfo(List<OnvifDevice> onvifDevices) throws InterruptedException {
        int noOfDevices = onvifDevices.size();
        CountDownLatch latch = new CountDownLatch(noOfDevices);
        OnvifManager onvifManager = new OnvifManager(new OnvifResponseListenerImpl(latch));

        Logger.log("GETTING SERVICE INFORMATION FROM " + noOfDevices + " DEVICES.");
        onvifDevices.forEach(onvifDevice -> {
            try {
                Logger.log("Request -- " + onvifDevice.getHostName());
                onvifManager.getServices(onvifDevice, (device, services) -> {
                    Logger.log("Success  -- " + onvifDevice.getHostName() + " -- " + services.getServicesPath());
                    data.put(device, services);
                    latch.countDown();
                });
            } catch (Exception e) {
                Logger.log("Failed  -- " + onvifDevice.getHostName() + " -- " + e.getMessage());
                latch.countDown();
            }
        });

        if (latch.await(awaitTimeoutInMinute, TimeUnit.MINUTES))
            Logger.log("GETTING SERVICE INFORMATION FOR " + noOfDevices + " DEVICES ENDED.");
        else
            Logger.log("GETTING SERVICE INFORMATION FOR " + noOfDevices + " DEVICES TIMED OUT.");
    }

    private void getDeviceInformation(List<OnvifDevice> onvifDevices) throws InterruptedException {
        int noOfDevices = onvifDevices.size();
        CountDownLatch latch = new CountDownLatch(noOfDevices);
        OnvifManager onvifManager = new OnvifManager(new OnvifResponseListenerImpl(latch));

        Logger.log("GETTING DEVICE INFORMATION FOR " + noOfDevices + " DEVICES.");
        onvifDevices.forEach(onvifDevice -> {
            try {
                Logger.log("Request -- " + onvifDevice.getHostName());
                onvifManager.getDeviceInformation(onvifDevice, (device, deviceInformation) -> {
                    Logger.log("Success  -- " + onvifDevice.getHostName() + " -- " + deviceInformation.getHardwareId());
                    data.put(device, deviceInformation);
                    latch.countDown();
                });
            } catch (Exception e) {
                Logger.log("Failed  -- " + onvifDevice.getHostName() + " -- " + e.getMessage());
                latch.countDown();
            }
        });

        if (latch.await(awaitTimeoutInMinute, TimeUnit.MINUTES))
            Logger.log("GETTING DEVICE INFORMATION FOR " + noOfDevices + " DEVICES ENDED.");
        else
            Logger.log("GETTING DEVICE INFORMATION FOR " + noOfDevices + " DEVICES TIMED OUT.");

    }

    private void getMediaProfiles(List<OnvifDevice> onvifDevices) throws InterruptedException {
        int noOfDevices = onvifDevices.size();
        CountDownLatch latch = new CountDownLatch(noOfDevices);
        OnvifManager onvifManager = new OnvifManager(new OnvifResponseListenerImpl(latch));

        Logger.log("GETTING MEDIA PROFILE INFORMATION FOR " + noOfDevices + " DEVICES.");
        onvifDevices.forEach(onvifDevice -> {
            try {
                Logger.log("Request -- " + onvifDevice.getHostName());
                onvifManager.getMediaProfiles(onvifDevice, (device, mediaProfiles) -> {
                    Logger.log("Success  -- " + onvifDevice.getHostName() + " -- " + mediaProfiles.size());
                    data.put(device, mediaProfiles);
                    latch.countDown();
                });
            } catch (Exception e) {
                Logger.log("Failed  -- " + onvifDevice.getHostName() + " -- " + e.getMessage());
                latch.countDown();
            }
        });

        if (latch.await(awaitTimeoutInMinute, TimeUnit.MINUTES))
            Logger.log("GETTING MEDIA PROFILE INFORMATION FOR " + noOfDevices + " DEVICES ENDED.");
        else
            Logger.log("GETTING MEDIA PROFILE INFORMATION FOR " + noOfDevices + " DEVICES TIMED OUT.");
    }

    private void getMediaStreamURI(List<OnvifDevicesData.MediaProfileList> profiles) throws InterruptedException {
        int noOfProfiles = profiles.size();

        if (noOfProfiles > 0) {
            CountDownLatch latch = new CountDownLatch(noOfProfiles);
            OnvifManager onvifManager = new OnvifManager(new OnvifResponseListenerImpl(latch));

            Logger.log("GETTING MEDIA STREAM URI INFORMATION FOR " + noOfProfiles + " PROFILES.");
            profiles.forEach(profile -> {
                try {
                    Logger.log("Request -- " + profile.onvifDevice.getHostName() + " -- " + profile.mediaProfile.getName());
                    onvifManager.getMediaStreamURI(profile.onvifDevice, profile.mediaProfile, (device, mediaProfile, uri) -> {
                        Logger.log("Success  -- " + profile.onvifDevice.getHostName() + " -- " + profile.mediaProfile.getName() + " -- " + uri);
                        data.put(device, mediaProfile, uri);
                        latch.countDown();
                    });
                } catch (Exception e) {
                    Logger.log("Failed   -- " + profile.onvifDevice.getHostName() + " -- " + profile.mediaProfile.getName() + " -- " + e.getMessage());
                    latch.countDown();
                }
            });

            if (latch.await(awaitTimeoutInMinute, TimeUnit.MINUTES))
                Logger.log("GETTING MEDIA STREAM URI INFORMATION FOR " + noOfProfiles + " DEVICES ENDED.");
            else
                Logger.log("GETTING MEDIA STREAM URI INFORMATION FOR " + noOfProfiles + " DEVICES TIMED OUT.");
        }
    }

    public int inquire(List<OnvifDevice> onvifDevices) {
        try {
            getServicesInfo(onvifDevices);
            getDeviceInformation(onvifDevices);
            getMediaProfiles(onvifDevices);
            getMediaStreamURI(data.getProfiles());
        } catch (Exception e) {
            Logger.log(e);
        }

        data.dumpAsJson();
        return data.getCount();
    }

    public int inquireByHostname(List<String> hostNames) {
        if (authRequire)
            return inquire(hostNames.stream().map(h -> new OnvifDevice(h, authDetails.username, authDetails.password)).collect(Collectors.toList()));
        else
            return inquire(hostNames.stream().map(OnvifDevice::new).collect(Collectors.toList()));
    }

    public int inquireByFile(String file) {
        /* Return iterator from CSV or Excel for 3 columns and create device and get data. */
        Read.fromCsvOrExcel(file);
        Logger.log("File base device list is not implemented yet.");
        return 0;
    }
}
