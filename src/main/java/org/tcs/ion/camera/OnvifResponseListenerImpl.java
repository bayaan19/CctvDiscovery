package org.tcs.ion.camera;

import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.responses.OnvifResponse;
import org.tcs.ion.camera.util.Logger;

import java.util.concurrent.CountDownLatch;

public class OnvifResponseListenerImpl implements OnvifResponseListener {
    private final CountDownLatch latch;

    public OnvifResponseListenerImpl(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onResponse(OnvifDevice onvifDevice, OnvifResponse onvifResponse) {
        Logger.log("Response -- " + onvifDevice.getHostName() + " -- " + onvifResponse.getOnvifRequest().getType().name() + " -- " + onvifResponse.isSuccess());
        latch.countDown();
    }

    @Override
    public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
        Logger.log("Error    -- " + onvifDevice.getHostName() + " -- " + errorCode + " -- " + errorMessage);
        latch.countDown();
    }
}
