package com.rvirin.onvif.onvifcamera

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader

/**
 * Created by Remy Virin on 05/03/2018.
 * @MediaProfile: is used to store an Onvif media profile (token and name)
 * @OnvifMediaProfiles: provide the xml command to retrieve the profiles and its parser.
 */

class MediaProfile(val name: String, val token: String, val encoding: String)


class OnvifMediaProfiles {
    companion object {

        fun getProfilesCommand(): String {
            return "<GetProfiles xmlns=\"http://www.onvif.org/ver10/media/wsdl\"/>"
        }

        fun parseXML(toParse: String): List<MediaProfile> {
            val results = ArrayList<MediaProfile>()

            try {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val parser = factory.newPullParser()
                parser.setInput(StringReader(toParse))
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

        fun readProfile(parser: XmlPullParser): MediaProfile {
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

        fun readEncoder(parser: XmlPullParser): String {
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

        fun readText(parser: XmlPullParser): String {
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
    }
}