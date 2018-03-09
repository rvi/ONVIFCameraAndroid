package com.rvirin.onvif.onvifcamera

import android.os.AsyncTask
import android.util.Log


import com.rvirin.onvif.onvifcamera.OnvifMediaProfiles.Companion.getProfilesCommand
import com.rvirin.onvif.onvifcamera.OnvifMediaStreamURI.Companion.getStreamURICommand
import com.rvirin.onvif.onvifcamera.OnvifMediaStreamURI.Companion.parseStreamURIXML
import com.rvirin.onvif.onvifcamera.OnvifDeviceInformation.Companion.deviceInformationCommand
import com.rvirin.onvif.onvifcamera.OnvifDeviceInformation.Companion.deviceInformationToString
import com.rvirin.onvif.onvifcamera.OnvifDeviceInformation.Companion.parseDeviceInformationResponse
import com.rvirin.onvif.onvifcamera.OnvifHeaderBody.authorizationHeader
import com.rvirin.onvif.onvifcamera.OnvifHeaderBody.envelopeEnd

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


@JvmField var currentDevice = OnvifDevice("", "", "")

interface OnvifListener {
    fun requestPerformed(response: OnvifResponse)
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
    var parsingUIMessage = ""

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

class OnvifDevice(IPAddress: String, @JvmField val username: String, @JvmField val password: String) {

    var listener: OnvifListener? = null
    /// We use this variable to know if the connection has been successful (retrieve device information)
    var isConnected = false

    private val url = "http://$IPAddress/onvif/device_service"
    private val deviceInformation = OnvifDeviceInformation()

    var mediaProfiles: List<MediaProfile> = emptyList()

    var rtspURI : String? = null


    fun getDeviceInformation() {
        val request = OnvifRequest(deviceInformationCommand, OnvifRequest.Type.GetDeviceInformation)
        ONVIFcommunication().execute(request)
    }

    fun getProfiles() {
        val request = OnvifRequest(getProfilesCommand(), OnvifRequest.Type.GetProfiles)
        ONVIFcommunication().execute(request)
    }

    fun getStreamURI() {

        mediaProfiles.firstOrNull()?.let {
            val request = OnvifRequest(getStreamURICommand(it), OnvifRequest.Type.GetStreamURI)
            ONVIFcommunication().execute(request)
        }
    }

    fun getStreamURI(profile: MediaProfile) {
            val request = OnvifRequest(getStreamURICommand(profile), OnvifRequest.Type.GetStreamURI)
            ONVIFcommunication().execute(request)
    }

    /**
     * Communication in Async Task between Android and ONVIF camera
     */
    private inner class ONVIFcommunication : AsyncTask<OnvifRequest, Void, OnvifResponse>() {

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
                    authorizationHeader + onvifRequest.xmlCommand + envelopeEnd)

            /* Request to ONVIF device */
            var request: Request? = null
            try {
                //   val credentials = Credentials.basic("operator","Onv!f2018")
                request = Request.Builder()
                        .url(currentDevice.url)
                        .addHeader("Content-Type","text/xml; charset=utf-8")
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
                    Log.d("BODY", bodyToString(request))
                    Log.d("RESPONSE", response.toString())

                    if (response.code() != 200) {
                        val responseBody = response.body()!!.string()
                        val message = response.code().toString() + " - " + response.message() + "\n" + responseBody
                        result.updateResponse(false, message)
                    } else {
                        result.updateResponse(true, response.body()!!.string())
                        val uiMessage = parseOnvifResponses(result)
                        result.parsingUIMessage = uiMessage
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

            listener?.requestPerformed(result)
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
                isConnected = true
                if (parseDeviceInformationResponse(result.result!!, currentDevice.deviceInformation)) {
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