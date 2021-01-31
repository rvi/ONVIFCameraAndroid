package com.seanproctor.onvif.onvifcamera

/**
 * Created from https://www.onvif.org/ver10/device/wsdl/devicemgmt.wsdl
 *
 * GetDeviceInformation
 * Description:
 * This operation gets basic device information from the device.
 *
 * @param manufacturerName The manufactor of the device.
 * @param modelName The device model.
 * @param fwVersion The firmware version in the device.
 * @param serialNumber The serial number of the device.
 * @param hwID The hardware ID of the device.
 */

data class OnvifDeviceInformation(
    val manufacturerName: String,
    val modelName: String,
    val fwVersion: String,
    val serialNumber: String,
    val hwID: String,
) {
    override fun toString(): String = (
            "Device information:\n"
                    + "Manufacturer: $manufacturerName\n"
                    + "Model: $modelName\n"
                    + "FirmwareVersion: $fwVersion\n"
                    + "SerialNumber: $serialNumber\n"
            //parsedResult += "HardwareId: " + parsed.hwID + "\n";
            )
}
