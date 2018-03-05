package com.onvif.onvifcamera_android.Onvif;

public class OnvifPtzConfiguration {

	public static String getConfigCommand(String profileToken) {
		String contStopCmd = "<GetConfiguration xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"/>";
		contStopCmd += "<PTZConfigurationToken  >" + profileToken + "</PTZConfigurationToken  >";
		return contStopCmd;
	}
}
