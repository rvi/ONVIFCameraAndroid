package com.onvif.onvifcamera_android.Onvif

import android.os.AsyncTask
import android.util.Log
import com.onvif.onvifcamera_android.Onvif.OnvifDeviceInformation.*

import com.onvif.onvifcamera_android.Onvif.OnvifHeaderBody.getAuthorizationHeader
import com.onvif.onvifcamera_android.Onvif.OnvifHeaderBody.getEnvelopeEnd
import com.onvif.onvifcamera_android.Onvif.OnvifMediaProfiles.Companion.getProfilesCommand
import com.onvif.onvifcamera_android.Onvif.OnvifMediaStreamURI.Companion.getStreamURICommand
import com.onvif.onvifcamera_android.Onvif.OnvifMediaStreamURI.Companion.parseStreamURIXML

import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by Remy Virin on 04/03/2018.
 * This class represent an ONVIF device and contains the methods to interact with it
 * (getDeviceInformation, getProfiles and getStreamURI).
 * @OnvifRequest: helps us to build the request
 * @OnvifResponse: contains the response from the Onvif device
 */

@JvmField
var currentDevice = OnvifDevice("", "", "")

interface OnvifUI {
    fun requestPerformed(requestType: OnvifRequest.Type, uiMessage: String)
}


class OnvifRequest(val xmlCommand: String, val type: Type) {

    enum class Type {
        GetDeviceInformation,
        GetProfiles,
        GetStreamURI;
    }
}

class OnvifResponse(val request: OnvifRequest) {

    var success = false
        private set

    private var message = ""

    fun updateResponse(success: Boolean, message: String) {
        this.success = success
        this.message = message
    }

    var result: String? = null
        get() {
            if (success) return message
            else return null
        }

    var error: String? = null
        get() {
            if (!success) return message
            else return null
        }

}

class OnvifDevice(IPAdress: String, @JvmField val username: String, @JvmField val password: String) {

    var delegate: OnvifUI? = null

    val url = "http://$IPAdress/onvif/device_service"
    private val deviceInformation = OnvifDeviceInformation()

    var mediaProfiles: List<MediaProfile> = emptyList()

    var rtspURI : String? = null


    fun getDeviceInformation() {
        val request = OnvifRequest(getDeviceInformationCommand(), OnvifRequest.Type.GetDeviceInformation)
        ONVIFcommunication().execute(request)
    }

    fun getProfiles() {
        val request = OnvifRequest(getProfilesCommand(), OnvifRequest.Type.GetProfiles)
        ONVIFcommunication().execute(request)
    }

    fun getStreamURI() {

        mediaProfiles.lastOrNull()?.let {
            val request = OnvifRequest(getStreamURICommand(it), OnvifRequest.Type.GetStreamURI)
            ONVIFcommunication().execute(request)
        }
    }

    /**
     * Communication in Async Task between Android and ONVIF camera
     */
    private inner class ONVIFcommunication : AsyncTask<OnvifRequest, String, OnvifResponse>() {

        /**
         * Background process of communication
         *
         * @param params
         * params[0] = soapRequest as string
         * params[1] = type of request (get, set or movement)
         * params[2] = flag for initialization requests
         * @return `String`
         * SOAP XML response from ONVIF device
         */
        override fun doInBackground(vararg params: OnvifRequest): OnvifResponse {
            val onvifRequest = params[0]

            val client = OkHttpClient.Builder()
                    .connectTimeout(10000, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(10000, TimeUnit.SECONDS)
                    .build()

            val reqBodyType = MediaType.parse("application/soap+xml; charset=utf-8;")

            val reqBody = RequestBody.create(reqBodyType,
                    getAuthorizationHeader() + onvifRequest.xmlCommand + getEnvelopeEnd())

            /* Request to ONVIF device */
            var request: Request? = null
            try {
                //   val credentials = Credentials.basic("operator","Onv!f2018")
                request = Request.Builder()
                        .url(currentDevice.url)
                        //             .addHeader("Authorization", credentials)
                        .addHeader("content-type", "application/soap+xml; charset=UTF-8")
                        .post(reqBody)
                        .build()
            } catch (e: IllegalArgumentException) {
                Log.e("ERROR", e.message!!)
                e.printStackTrace()
            }

            val result = OnvifResponse(onvifRequest)

            if (request != null) {
                try {
                    /* Response from ONVIF device */
                    val response = client.newCall(request).execute()
                    Log.e("BODY", bodyToString(request))
                    Log.e("RESPONSE", response.toString())

                    if (response.code() != 200) {

                        val message = response.code().toString() + " - " + response.message()
                        result.updateResponse(false, message)
                    } else {

                        result.updateResponse(true, response.body()!!.string())
                    }
                } catch (e: IOException) {
                    result.updateResponse(false, e.message!!)
                }
            }

            return result
        }

        private fun bodyToString(request: Request): String {

            try {
                val copy = request.newBuilder().build()
                val buffer = Buffer()
                copy.body()!!.writeTo(buffer)
                return buffer.readUtf8()
            } catch (e: IOException) {
                return "did not work"
            }

        }

        /**
         * Called when AsyncTask background process is finished
         *
         * @param result
         * String result of communication
         */
        override fun onPostExecute(result: OnvifResponse) {
            Log.d("RESULT", result.success.toString())
            val uiMessage = parseOnvifResponses(result)

            delegate?.requestPerformed(result.request.type, uiMessage)
        }
    }

    private fun appendCredentials(streamURI: String): String {
        val protocol = "rtsp://"
        val startIndex = protocol.length
        val uri = streamURI.substring(startIndex)
        return protocol + currentDevice.username + ":" + currentDevice.password + "@" + uri
    }

    private fun parseOnvifResponses(result: OnvifResponse): String {
        var parsedResult = "Parsing failed"
        if (!result.success) {
            parsedResult = "Communication error trying to get " + result.request + ":\n\n" + result.error

        } else {
            if (result.request.type == OnvifRequest.Type.GetDeviceInformation) {
                if (parseDeviceInformationResponse(result.result, currentDevice.deviceInformation)) {
                    parsedResult = deviceInformationToString(currentDevice.deviceInformation)
                }

            } else if (result.request.type == OnvifRequest.Type.GetProfiles) {
                result.result?.let {
                    val profiles = OnvifMediaProfiles.parseXML(it)
                    currentDevice.mediaProfiles = profiles
                    parsedResult = profiles.count().toString() + " profiles retrieved."
                }

            } else if (result.request.type == OnvifRequest.Type.GetStreamURI) {
                result.result?.let {
                    val streamURI = parseStreamURIXML(it)
                    currentDevice.rtspURI = appendCredentials(streamURI)
                    parsedResult = "RTSP URI retrieved."
                }
            }
        }

        return parsedResult
    }
}