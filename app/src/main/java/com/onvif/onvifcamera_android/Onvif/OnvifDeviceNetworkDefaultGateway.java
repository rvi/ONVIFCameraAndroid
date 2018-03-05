package com.onvif.onvifcamera_android.Onvif;

public class OnvifDeviceNetworkDefaultGateway {

	@SuppressWarnings("SameReturnValue")
	public static String getNetGatewayCommand() {
		return "<GetNetworkDefaultGateway xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>";
	}
}
