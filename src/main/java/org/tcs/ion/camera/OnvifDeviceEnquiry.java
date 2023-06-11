package org.tcs.ion.camera;

import be.teletask.onvif.OnvifManager;
import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.models.Device;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifMediaProfile;
import be.teletask.onvif.responses.OnvifResponse;
import org.tcs.ion.camera.models.OnvifDevicesData;
import org.tcs.ion.camera.util.Logger;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class OnvifDeviceEnquiry implements OnvifResponseListener {
    OnvifManager onvifManager;
    private CountDownLatch latch;
    OnvifDevicesData data;

    public OnvifDeviceEnquiry() {
        this.onvifManager = new OnvifManager(this);
        this.data = new OnvifDevicesData();
    }

    @Override
    public void onResponse(OnvifDevice onvifDevice, OnvifResponse onvifResponse) {
        Logger.log(onvifDevice.getHostName() + " " + onvifResponse.getOnvifRequest().getType().name() + " " + onvifResponse.isSuccess());
        latch.countDown();
    }

    @Override
    public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
        Logger.log(onvifDevice.getHostName() + " " + errorCode + " " + errorMessage);
        latch.countDown();
    }

    private void getDeviceInformation(OnvifDevice onvifDevice) {
        onvifManager.getDeviceInformation(onvifDevice, (device, deviceInformation) -> data.put(device, deviceInformation));
    }

    private void getServicesInfo(OnvifDevice onvifDevice) {
        onvifManager.getServices(onvifDevice, (device, services) -> data.put(device, services));
    }

    private void getMediaProfiles(OnvifDevice onvifDevice) {
        onvifManager.getMediaProfiles(onvifDevice, (device, mediaProfiles) -> data.put(device, mediaProfiles));
    }

    private void getMediaStreamURI(OnvifDevice onvifDevice, OnvifMediaProfile onvifMediaProfile) {
        onvifManager.getMediaStreamURI(onvifDevice, onvifMediaProfile, (device, mediaProfile, uri) -> data.put(device, mediaProfile, uri));
    }

    public int inquireByHostname(List<String> hostNames) {
        return inquire(hostNames.stream().map(OnvifDevice::new).collect(Collectors.toList()));
    }

    public int inquireByDevice(List<Device> devices) {
        return inquire(devices.stream().map(s -> new OnvifDevice(s.getHostName())).collect(Collectors.toList()));
    }

    public int inquire(List<OnvifDevice> onvifDevices) {
        try {
            int noOfDevices = onvifDevices.size();
            latch = new CountDownLatch(noOfDevices);
            Logger.log("GETTING SERVICE INFORMATION FROM " + noOfDevices + " DEVICES.");
            onvifDevices.forEach(this::getServicesInfo);
            latch.await();

            latch = new CountDownLatch(noOfDevices);
            Logger.log("GETTING DEVICE INFORMATION FOR " + noOfDevices + " DEVICES.");
            onvifDevices.forEach(this::getDeviceInformation);
            latch.await();

            latch = new CountDownLatch(noOfDevices);
            Logger.log("GETTING PROFILE INFORMATION FOR " + noOfDevices + " DEVICES.");
            onvifDevices.forEach(this::getMediaProfiles);
            latch.await();

            List<OnvifDevicesData.MediaProfileList> profiles = data.getProfiles();
            int noOfProfiles = profiles.size();
            if (noOfProfiles > 0) {
                latch = new CountDownLatch(noOfProfiles);
                Logger.log("GETTING MEDIA STREAM URI INFORMATION FOR " + noOfProfiles + " PROFILES.");
                profiles.forEach(item -> getMediaStreamURI(item.onvifDevice, item.mediaProfile));
                latch.await();
            }

            data.dumpAsJson();
        } catch (Exception e) {
            Logger.log(e);
        }
        return data.getCount();
    }
}
