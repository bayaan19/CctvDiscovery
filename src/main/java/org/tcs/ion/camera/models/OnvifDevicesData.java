package org.tcs.ion.camera.models;

import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifDeviceInformation;
import be.teletask.onvif.models.OnvifMediaProfile;
import be.teletask.onvif.models.OnvifServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tcs.ion.camera.util.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnvifDevicesData {
    Map<OnvifDevice, OnvifDeviceData> data = new HashMap<>();

    public int getCount() {
        return data.size();
    }

    public List<MediaProfileList> getProfiles() {
        List<MediaProfileList> mediaProfileList = new ArrayList<>();
        data.forEach((device, data) -> data.profiles().forEach(profile -> mediaProfileList.add(new MediaProfileList(device, profile))));
        return mediaProfileList;
    }

    public void put(OnvifDevice onvifDevice, OnvifDeviceInformation deviceInformation) {
        data.computeIfAbsent(onvifDevice, OnvifDeviceData::new).setDeviceInformation(deviceInformation);
    }

    public void put(OnvifDevice onvifDevice, OnvifServices services) {
        data.computeIfAbsent(onvifDevice, OnvifDeviceData::new).setServices(services);
    }

    public void put(OnvifDevice onvifDevice, List<OnvifMediaProfile> mediaProfiles) {
        data.computeIfAbsent(onvifDevice, OnvifDeviceData::new).setMediaProfiles(mediaProfiles);
    }

    public void put(OnvifDevice onvifDevice, OnvifMediaProfile mediaProfile, String uri) {
        data.computeIfAbsent(onvifDevice, OnvifDeviceData::new).setMediaProfiles(mediaProfile, uri);
    }

    public String getAsJson() {
        return new Gson().toJson(data.values());
    }

    public void dumpAsJson() {
        String fileName = "OnvifDevicesData-" + Logger.currentTimestamp() + ".json";
        try (Writer writer = new FileWriter(fileName)) {
            new GsonBuilder().create().toJson(data.values(), writer);
            Logger.msg("ONVIF device(s) data dumped in file: " + fileName);
        } catch (IOException e) {
            Logger.log(e);
        }
    }

    public static class MediaProfileList {
        public OnvifDevice onvifDevice;
        public OnvifMediaProfile mediaProfile;

        public MediaProfileList(OnvifDevice onvifDevice, OnvifMediaProfile mediaProfile) {
            this.onvifDevice = onvifDevice;
            this.mediaProfile = mediaProfile;
        }
    }
}
