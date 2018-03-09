package com.rvirin.onvif.onvifcamera
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
 * Manufacturer [string] - The manufactor of the device.
 * Model [string] - The device model.
 * FirmwareVersion [string] - The firmware version in the device.
 * SerialNumber [string] - The serial number of the device.
 * HardwareId [string] - The hardware ID of the device.
 */

class OnvifDeviceInformation {
    private var manufacturerName = "unknown"
    private var modelName = "unknown"
    private var fwVersion = "unknown"
    private var serialNumber = "unknown"
    private var hwID = "unknown"

    companion object {

        val deviceInformationCommand: String
            get() =
                "<GetDeviceInformation xmlns=\"http://www.onvif.org/ver10/device/wsdl\">" + "</GetDeviceInformation>"

        fun parseDeviceInformationResponse(response: String, parsed: OnvifDeviceInformation): Boolean {
            try {
                OnvifResponseParser.lastIndex = 0 // Start from beginning of response
                parsed.manufacturerName = OnvifResponseParser.parseOnvifString("facturer>", "</tds", response)
                parsed.modelName = OnvifResponseParser.parseOnvifString("Model>", "</tds", response)
                parsed.fwVersion = OnvifResponseParser.parseOnvifString("Version>", "</tds", response)
                parsed.serialNumber = OnvifResponseParser.parseOnvifString("Number>", "</tds", response)
                parsed.hwID = OnvifResponseParser.parseOnvifString("reId>", "</tds", response)
                return true
            } catch (ignore: StringIndexOutOfBoundsException) {
                return false
            }

        }

        fun deviceInformationToString(parsed: OnvifDeviceInformation): String {
            var parsedResult = "Device information:\n"
            parsedResult += "Manufacturer: " + parsed.manufacturerName + "\n"
            parsedResult += "Model: " + parsed.modelName + "\n"
            parsedResult += "FirmwareVersion: " + parsed.fwVersion + "\n"
            parsedResult += "SerialNumber: " + parsed.serialNumber + "\n"
            //parsedResult += "HardwareId: " + parsed.hwID + "\n";
            return parsedResult
        }
    }
}
