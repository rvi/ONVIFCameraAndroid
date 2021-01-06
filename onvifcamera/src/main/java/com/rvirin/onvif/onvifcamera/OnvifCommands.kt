package com.rvirin.onvif.onvifcamera

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader

fun getStreamURICommand(profile: MediaProfile): String {

    return ("<GetStreamUri xmlns=\"http://www.onvif.org/ver20/media/wsdl\">"
            + "<ProfileToken>" + profile.token + "</ProfileToken>"
            + "<Protocol>RTSP</Protocol>"
            + "</GetStreamUri>")
}

fun parseStreamURIXML(toParse: String): String {
    var result = ""

    try {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(toParse))
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

fun getSnapshotURICommand(profile: MediaProfile): String {

    return ("<GetSnapshotUri xmlns=\"http://www.onvif.org/ver20/media/wsdl\">"
            + "<ProfileToken>${profile.token}</ProfileToken>"
            + "</GetSnapshotUri>")
}
