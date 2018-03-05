package com.onvif.onvifcamera_android.Onvif;

public class OnvifDeviceNetworkInterfaces {

	private String netInterfaceToken = "unknown";
	private boolean netInterfaceEnabled = false;
	private String infoName = "unknown";
	private String infoHwAddress = "unknown";
	private boolean ip4Enabled = false;
	private String fromDHCPaddress = "unknown";
	private int fromDHCPPrefix = 0;
	private boolean dhcpEnabled = false;

	@SuppressWarnings("SameReturnValue")
	public static String getNetInterfacesCommand() {
		return "<GetNetworkInterfaces xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>";
	}

	public static boolean parseNetworkInterfacesResponse(String response, OnvifDeviceNetworkInterfaces parsed) {
		try {
			OnvifResponseParser.lastIndex = 0; // Start from beginning of response
			parsed.netInterfaceToken = OnvifResponseParser.parseOnvifString("token=\"", "\">", response);
			parsed.netInterfaceEnabled = OnvifResponseParser.parseOnvifBoolean("Enabled>", "</tt", response);
			parsed.infoName = OnvifResponseParser.parseOnvifString("Name>", "</tt", response);
			parsed.infoHwAddress = OnvifResponseParser.parseOnvifString("HwAddress>", "</tt", response);
			parsed.ip4Enabled = OnvifResponseParser.parseOnvifBoolean("Enabled>", "</tt", response);
			parsed.fromDHCPaddress = OnvifResponseParser.parseOnvifString("Address>", "</tt", response);
			parsed.fromDHCPPrefix = OnvifResponseParser.parseOnvifInt("Length>", "</tt", response);
			parsed.dhcpEnabled = OnvifResponseParser.parseOnvifBoolean(":DHCP>", "</tt", response);
			return true;
		} catch (StringIndexOutOfBoundsException ignore) {
			return false;
		}
	}

	public static String interfacesToString(OnvifDeviceNetworkInterfaces parsed) {
		String parsedResult = "Interfaces:\n";
		parsedResult += "Token: " + parsed.netInterfaceToken + "\n";
		parsedResult += "Enabled: " + parsed.netInterfaceEnabled + "\n";
		parsedResult += "Name: " + parsed.infoName + "\n";
		parsedResult += "HW address: " + parsed.infoHwAddress + "\n";
		parsedResult += "IP4 enabled: " + parsed.ip4Enabled + "\n";
		parsedResult += "DHCP assigned address: " + parsed.fromDHCPaddress + "\n";
		parsedResult += "DHCP prefix length: " + parsed.fromDHCPPrefix + "\n";
		parsedResult += "DHCP enabled: " + parsed.dhcpEnabled + "\n";
		return parsedResult;
	}
}
