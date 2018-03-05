package com.onvif.onvifcamera_android.Onvif;

public class OnvifPtzNode {

	public static String getNodeCommand(String profileToken) {
		return "<GetNode xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"/><NodeToken>" + profileToken + "</NodeToken>";
	}
}
