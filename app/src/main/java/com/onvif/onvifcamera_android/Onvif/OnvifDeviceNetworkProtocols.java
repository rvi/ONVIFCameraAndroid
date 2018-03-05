package com.onvif.onvifcamera_android.Onvif;

public class OnvifDeviceNetworkProtocols {

	@SuppressWarnings("SameReturnValue")
	public static String getNetProtocolsCommand() {
		return "<GetNetworkProtocols xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>";
	}

}
