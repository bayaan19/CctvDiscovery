package org.tcs.ion.camera.models;

import be.teletask.onvif.models.OnvifMediaProfile;

public class OnvifMediaProfileData {
    OnvifMediaProfile mediaProfile;
    String uri;

    public OnvifMediaProfileData(OnvifMediaProfile mediaProfile) {
        this.mediaProfile = mediaProfile;
    }

    public OnvifMediaProfile getMediaProfile() {
        return mediaProfile;
    }

    public void setMediaProfile(OnvifMediaProfile mediaProfile) {
        this.mediaProfile = mediaProfile;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
