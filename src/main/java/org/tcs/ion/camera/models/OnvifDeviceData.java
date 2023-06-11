package org.tcs.ion.camera.models;

import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifDeviceInformation;
import be.teletask.onvif.models.OnvifMediaProfile;
import be.teletask.onvif.models.OnvifServices;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OnvifDeviceData {

    OnvifDevice onvifDevice;
    List<OnvifMediaProfileData> mediaProfiles;
    OnvifDeviceInformation deviceInformation;
    OnvifServices services;

    public OnvifDeviceData(OnvifDevice onvifDevice) {
        this.onvifDevice = onvifDevice;
        this.mediaProfiles = new ArrayList<>();
    }

    public List<OnvifMediaProfile> profiles() {
        return mediaProfiles.stream().map(OnvifMediaProfileData::getMediaProfile).collect(Collectors.toList());
    }
    public List<OnvifMediaProfileData> getMediaProfiles() {
        return mediaProfiles;
    }

    public void setMediaProfiles(List<OnvifMediaProfile> mediaProfiles) {
        this.mediaProfiles.addAll(mediaProfiles.stream().map(OnvifMediaProfileData::new).collect(Collectors.toList()));
    }
    public void setMediaProfiles(OnvifMediaProfile mediaProfile, String uri) {
        mediaProfiles.forEach(item -> {
            if (item.getMediaProfile().equals(mediaProfile))
                item.setUri(uri);
         });
    }

    public void setMediaProfiles(OnvifMediaProfile mediaProfile) {
        this.mediaProfiles.add(new OnvifMediaProfileData(mediaProfile));
    }

    public OnvifDeviceInformation getDeviceInformation() {
        return deviceInformation;
    }

    public void setDeviceInformation(OnvifDeviceInformation deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public OnvifServices getServices() {
        return services;
    }

    public void setServices(OnvifServices services) {
        this.services = services;
    }
}
