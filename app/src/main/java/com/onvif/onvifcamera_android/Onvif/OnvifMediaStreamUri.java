package com.onvif.onvifcamera_android.Onvif;

import android.provider.MediaStore;

import java.net.URI;

import static com.onvif.onvifcamera_android.Onvif.OnvifResponseParser.timeOutToMS;

public class OnvifMediaStreamUri {

    private  String mediaUri = "unknown";
    private String streamTimeout = "unknown";
    private int streamTimeoutMS = 0;
    private int mediaRtspPort = 0;

    @SuppressWarnings("SameReturnValue")
    public static String getStreamUriCommand(MediaProfile profile) {
        return "<GetStreamUri xmlns=\"http://www.onvif.org/ver20/media/wsdl\">"
                + "<ProfileToken>" + profile.getToken() + "</ProfileToken>"
                + "<Protocol>RTSP</Protocol>"
                + "</GetStreamUri>";

    }

    public static boolean parseStreamUriResponse(String response, OnvifMediaStreamUri parsed) {
        try {
            OnvifResponseParser.lastIndex = 0; // Start from beginning of response
            URI foundURI = URI.create(OnvifResponseParser.parseOnvifString(":Uri>", "</tt", response));
            parsed.mediaUri = foundURI.getPath();
            parsed.mediaRtspPort = foundURI.getPort();
            parsed.streamTimeout = OnvifResponseParser.parseOnvifString("Timeout>", "</tt", response);
            parsed.streamTimeoutMS = timeOutToMS(parsed.streamTimeout);
            return true;
        } catch (StringIndexOutOfBoundsException ignore) {
            return false;
        }
    }

    public static String streamUriToString(OnvifMediaStreamUri parsed) {
        String parsedResult = "Video stream URI:\n";
        parsedResult += "Stream URL: " + parsed.mediaUri + "\n";
        parsedResult += "Stream Port: " + parsed.mediaRtspPort + "\n";
        parsedResult += "Timeout: " + parsed.streamTimeout + "\n";
        parsedResult += "Timeut in ms: " + parsed.streamTimeoutMS + "\n";
        return parsedResult;
    }
}
