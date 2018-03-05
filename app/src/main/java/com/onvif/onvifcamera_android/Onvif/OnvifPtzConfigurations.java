package com.onvif.onvifcamera_android.Onvif;

public class OnvifPtzConfigurations {

	@SuppressWarnings("SameReturnValue")
	public static String getConfigsCommand() {
		return "<tds:GetConfigurations></tds:GetConfigurations>";
	}
}
