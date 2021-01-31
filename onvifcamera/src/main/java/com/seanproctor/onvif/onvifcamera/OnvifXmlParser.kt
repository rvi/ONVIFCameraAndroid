package com.seanproctor.onvif.onvifcamera

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

object OnvifXmlParser {

    fun parseProfilesResponse(input: InputStream): List<MediaProfile> {
        val results = ArrayList<MediaProfile>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(input, null)
            parser.nextTag()
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "Profiles") {
                    results.add(readProfile(parser))
                }
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return results
    }

    private fun readProfile(parser: XmlPullParser): MediaProfile {
        // parser.require(XmlPullParser.START_TAG, )
        val token = parser.getAttributeValue(null, "token")
        var name = ""
        var encoding = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            when (parser.name) {
                "Name" -> name = readText(parser)
                "VideoEncoderConfiguration" -> encoding = readEncoder(parser)
                else -> skip(parser)
            }
        }
        return MediaProfile(name, token, encoding)
    }

    private fun readEncoder(parser: XmlPullParser): String {
        var encoding = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.name == "Encoding") {
                encoding = readText(parser)
            } else {
                skip(parser)
            }
        }
        return encoding
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    fun parseStreamURIXML(input: InputStream): String {
        var result = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(input, null)
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG && xpp.name == "Uri") {

                    xpp.next()
                    result = xpp.text
                    break
                }
                eventType = xpp.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return result
    }

    fun parseServicesResponse(input: InputStream): Map<String, String> {
        val namespaceMap = mutableMapOf<String, String>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(input, null)
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG && xpp.name == "Namespace") {
                    xpp.next()
                    val currentNamespace = xpp.text
                    val uri = retrieveXAddr(xpp)
                    namespaceMap[currentNamespace] = retrievePath(uri)
                }

                eventType = xpp.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return namespaceMap
    }

    /**
     * Util method to retrieve a path from an URL (without IP address and port)
     * @example:
     * @param uri example input: `http://192.168.1.0:8791/cam/realmonitor?audio=1`
     * @result example output: `cam/realmonitor?audio=1`
     */
    private fun retrievePath(uri: String): String {
        var url: URL
        try {
            url = URL(uri)
        } catch (ex: MalformedURLException) {
            val index = uri.indexOf(':')
            url = URL("http" + uri.drop(index))
        }

        var result = url.path

        if (url.query != null) {
            result += url.query
        }

        return result
    }

    /**
     * Util method for parsing. Retrieve the XAddr from the XmlPullParser given.
     */
    private fun retrieveXAddr(xpp: XmlPullParser): String {

        var result = ""

        var eventType = xpp.eventType
        while (eventType != XmlPullParser.END_DOCUMENT ||
            (eventType == XmlPullParser.END_TAG && xpp.name == "Service")
        ) {

            if (eventType == XmlPullParser.START_TAG && xpp.name == "XAddr") {
                xpp.next()
                result = xpp.text
                break
            }
            eventType = xpp.next()
        }
        return result
    }

    fun parseDeviceInformationResponse(input: InputStream): OnvifDeviceInformation {
        var manufacturerName = "unknown"
        var modelName = "unknown"
        var fwVersion = "unknown"
        var serialNumber = "unknown"
        var hwID = "unknown"

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(input, null)
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG && xpp.name == "Manufacturer") {
                    xpp.next()
                    manufacturerName = xpp.text
                } else if (eventType == XmlPullParser.START_TAG && xpp.name == "Model") {
                    xpp.next()
                    modelName = xpp.text
                } else if (eventType == XmlPullParser.START_TAG && xpp.name == "FirmwareVersion") {
                    xpp.next()
                    fwVersion = xpp.text
                } else if (eventType == XmlPullParser.START_TAG && xpp.name == "SerialNumber") {
                    xpp.next()
                    serialNumber = xpp.text
                } else if (eventType == XmlPullParser.START_TAG && xpp.name == "HardwareId") {
                    xpp.next()
                    hwID = xpp.text
                }
                eventType = xpp.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return OnvifDeviceInformation(
            manufacturerName = manufacturerName,
            modelName = modelName,
            fwVersion = fwVersion,
            serialNumber = serialNumber,
            hwID = hwID
        )
    }
}