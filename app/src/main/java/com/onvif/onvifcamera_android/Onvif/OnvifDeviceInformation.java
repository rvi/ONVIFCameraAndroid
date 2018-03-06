package com.onvif.onvifcamera_android.Onvif;

/**
 * Created from https://www.onvif.org/ver10/device/wsdl/devicemgmt.wsdl
 *
 * GetDeviceInformation
 * Description:
 * This operation gets basic device information from the device.
 *
 * Input:
 * [GetDeviceInformation]
 *
 * Output:
 * [GetDeviceInformationResponse]
 *  Manufacturer [string] - The manufactor of the device.
 *  Model [string] - The device model.
 *  FirmwareVersion [string] - The firmware version in the device.
 *  SerialNumber [string] - The serial number of the device.
 *  HardwareId [string] - The hardware ID of the device.
 */

 public class OnvifDeviceInformation {
	private String manufacturerName = "unknown";
	private String modelName = "unknown";
	private String fwVersion = "unknown";
	private String serialNumber = "unknown";
	private String hwID = "unknown";

	@SuppressWarnings("SameReturnValue")
	public static String getDeviceInformationCommand() {
		return "<GetDeviceInformation xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>";
	}

	public static boolean parseDeviceInformationResponse(String response, OnvifDeviceInformation parsed) {
		try {
			OnvifResponseParser.lastIndex = 0; // Start from beginning of response
			parsed.manufacturerName = OnvifResponseParser.parseOnvifString("facturer>", "</tds", response);
			parsed.modelName = OnvifResponseParser.parseOnvifString("Model>", "</tds", response);
			parsed.fwVersion = OnvifResponseParser.parseOnvifString("Version>", "</tds", response);
			parsed.serialNumber = OnvifResponseParser.parseOnvifString("Number>", "</tds", response);
			parsed.hwID = OnvifResponseParser.parseOnvifString("reId>", "</tds", response);
			return true;
		} catch (StringIndexOutOfBoundsException ignore) {
			return false;
		}
	}

	public static String deviceInformationToString(OnvifDeviceInformation parsed) {
		String parsedResult = "Device information:\n";
		parsedResult += "Manufacturer: " + parsed.manufacturerName + "\n";
		parsedResult += "Model: " + parsed.modelName + "\n";
		parsedResult += "FirmwareVersion: " + parsed.fwVersion + "\n";
		parsedResult += "SerialNumber: " + parsed.serialNumber + "\n";
		//parsedResult += "HardwareId: " + parsed.hwID + "\n";
		return parsedResult;
	}
}
