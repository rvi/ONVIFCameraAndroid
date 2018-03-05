package com.onvif.onvifcamera_android.Onvif;

public class OnvifMediaOSDs {

	public static String getOSDsCommand(String profileToken) {
		String getOsdsCmd = "<GetOSDs xmlns=\"http://www.onvif.org/ver10/media/wsdl\"/>";
		getOsdsCmd += "<ConfigurationToken >" + profileToken + "</ConfigurationToken >";
		return getOsdsCmd;
	}
}
