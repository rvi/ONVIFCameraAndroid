package com.onvif.onvifcamera_android.Onvif;

public class OnvifPtzStop {

	public static String getStopCommand(String profileToken) {
		return "<Stop xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
				"<ProfileToken>" + profileToken + "</ProfileToken>" +
				"<PanTilt>" +
				"true" +
				"</PanTilt>" +
				"<Zoom>" +
				"true" +
				"</Zoom>" +
				"</Stop>";
	}
}
