package com.onvif.onvifcamera_android.Onvif

import android.os.AsyncTask
import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import com.onvif.onvifcamera_android.Onvif.OnvifDeviceInformation.getDeviceInformationCommand
import com.onvif.onvifcamera_android.Onvif.OnvifHeaderBody.getAuthorizationHeader
import com.onvif.onvifcamera_android.Onvif.OnvifHeaderBody.getEnvelopeEnd
import com.onvif.onvifcamera_android.Onvif.OnvifMediaProfiles.getProfilesCommand
import com.onvif.onvifcamera_android.Onvif.OnvifMediaStreamUri.getStreamUriCommand

import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by remy on 04/03/2018.
 */

@JvmField
var currentDevice = OnvifDevice("", "", "")

interface OnvifUI {
    fun updateUI(message: String)
}


enum class OnvifRequest {
    getDeviceInformation,
    getProfiles,
    getStreamingURI;

    fun xmlCommand(): String {
        when (this) {
            getDeviceInformation -> return getDeviceInformationCommand()
            getProfiles -> return getProfilesCommand()
            getStreamingURI -> return getStreamUriCommand()
        }
    }
}

class OnvifResponse(success: Boolean, message: String) {
    var success = success
    private var message = message

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

class OnvifDevice(IPAdress: String, username: String, password: String) {

    var delegate: OnvifUI? = null

    val url = "http://$IPAdress/onvif/device_service"
    @JvmField
    var username = username
    @JvmField
    val password = password

    var rtspURL = ""


    fun getDeviceInformation() {

        ONVIFcommunication().execute(OnvifRequest.getDeviceInformation)
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
                    getAuthorizationHeader() + onvifRequest.xmlCommand() + getEnvelopeEnd())

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

            var result = OnvifResponse(false,"not initialized")

            if (request != null) {
                try {
                    /* Response from ONVIF device */
                    val response = client.newCall(request).execute()
                    Log.e("BODY", bodyToString(request))
                    Log.e("RESPONSE", response.toString())

                    if (response.code() !== 200) {

                        val message = response.code().toString() + " - " + response.message()
                        result = OnvifResponse(false, message)
                    } else {

                        result = OnvifResponse(true, response.body()!!.string())
                    }
                } catch (e: IOException) {
                    result = OnvifResponse(false, e.message!!)
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
            delegate?.updateUI("YOOOOOO!!")
            // parseOnvifResponses(result)
        }
    }
/*
    private fun parseOnvifResponses(result: Array<String>) {
        var parsedResult = "Parsing failed"
        if (result[3].equals("failed", ignoreCase = true)) {
            parsedResult = "Communication error trying to get " + result[0] + ":\n\n" + result[4]
            //			tvStartup.setText(parsedResult);
            val mySnackbar = Snackbar.make(findViewById<View>(android.R.id.content),
                    getString(R.string.COMM_FAIL),
                    Snackbar.LENGTH_LONG)
            val snackbarView = mySnackbar.getView()
            val tv = snackbarView.findViewById(android.support.design.R.id.snackbar_text) as TextView
            tv.maxLines = 300
            mySnackbar.show()
        } else {
            if (result[1].equals("devinfo", ignoreCase = true)) {
                if (parseDeviceInformationResponse(result[4], selectedDevice.devInfo)) {
                    parsedResult = deviceInformationToString(selectedDevice.devInfo)
                }
            } else if (result[1].equals("scopes", ignoreCase = true)) {
                if (parseScopesResponse(result[4], selectedDevice.scopes)) {
                    parsedResult = scopesToString(selectedDevice.scopes)
                }
            } else if (result[1].equals("capabilities", ignoreCase = true)) {
                if (parseCapabilitiesResponse(result[4], selectedDevice.devCapabilities)) {
                    parsedResult = capabilitiesToString(selectedDevice.devCapabilities)
                }
            } else if (result[1].equals("profiles", ignoreCase = true)) {
                var part1Profile = result[4].substring(result[4].indexOf("<trt:Profiles token"),
                        result[4].indexOf("</trt:Profiles>") + 15)
                if (parseProfilesResponse(part1Profile, selectedDevice.mediaProfiles[0])) {
                    parsedResult = profilesToString(selectedDevice.mediaProfiles[0])
                }
                part1Profile = result[4].substring(result[4].lastIndexOf("<trt:Profiles token"),
                        result[4].lastIndexOf("</trt:Profiles>") + 15)
                if (parseProfilesResponse(part1Profile, selectedDevice.mediaProfiles[1])) {
                    parsedResult += profilesToString(selectedDevice.mediaProfiles[1])
                }
            } else if (result[1].equals("netgate", ignoreCase = true)) {
                parsedResult = simpleSoapFormatter(
                        result[4].substring(
                                result[4].indexOf("<SOAP-ENV:Body>") + 15,
                                result[4].indexOf("</SOAP-ENV:Body>")))
                //				if (parseNetworkDefaultGatewayResponse(result[4], selectedDevice.devDefaultGateway)) {
                //					parsedResult = defaultGatewayToString(selectedDevice.devDefaultGateway);
                //				}
            } else if (result[1].equals("dns", ignoreCase = true)) {
                parsedResult = simpleSoapFormatter(
                        result[4].substring(
                                result[4].indexOf("<SOAP-ENV:Body>") + 15,
                                result[4].indexOf("</SOAP-ENV:Body>")))
                //				if (parseDNSResponse(result[4], selectedDevice.devDNS)) {
                //					parsedResult = dnsToString(selectedDevice.devDNS);
                //				}
            } else if (result[1].equals("ifaces", ignoreCase = true)) {
                if (parseNetworkInterfacesResponse(result[4], selectedDevice.devNetInterface)) {
                    parsedResult = interfacesToString(selectedDevice.devNetInterface)
                }
            } else if (result[1].equals("netproto", ignoreCase = true)) {
                parsedResult = simpleSoapFormatter(
                        result[4].substring(
                                result[4].indexOf("<SOAP-ENV:Body>") + 15,
                                result[4].indexOf("</SOAP-ENV:Body>")))
                //				if (parseNetworkProtocolResponse(result[4], selectedDevice.devNetProtocols)) {
                //					parsedResult = netProtocolToString(selectedDevice.devNetProtocols);
                //				}
            } else if (result[1].equals("streamuri", ignoreCase = true)) {
                if (parseStreamUriResponse(result[4], selectedDevice.mediaStreamUri)) {
                    parsedResult = streamUriToString(selectedDevice.mediaStreamUri)
                }
            } else if (result[1].equals("osds", ignoreCase = true)) {
                parsedResult = simpleSoapFormatter(
                        result[4].substring(
                                result[4].indexOf("<SOAP-ENV:Body>") + 15,
                                result[4].indexOf("</SOAP-ENV:Body>")))
                //				if (parseOSDSResponse(result[4], selectedDevice.mediaOSDs)) {
                //					parsedResult = osdsToString(selectedDevice.mediaOSDs);
                //				}
            } else if (result[1].equals("getNode", ignoreCase = true)) {
                parsedResult = simpleSoapFormatter(
                        result[4].substring(
                                result[4].indexOf("<SOAP-ENV:Body>") + 15,
                                result[4].indexOf("</SOAP-ENV:Body>")))
                //				if (parsePtzNodeResponse(result[4], selectedDevice.ptzNode)) {
                //					parsedResult = ptzNodeToString(selectedDevice.ptzNode);
                //				}
            } else if (result[1].equals("getNodes", ignoreCase = true)) {
                parsedResult = simpleSoapFormatter(
                        result[4].substring(
                                result[4].indexOf("<SOAP-ENV:Body>") + 15,
                                result[4].indexOf("</SOAP-ENV:Body>")))
                //				if (parsePtzNodesResponse(result[4], selectedDevice.ptzNodes)) {
                //					parsedResult = ptzNodesToString(selectedDevice.ptzNodes);
                //				}
            } else if (result[1].equals("getConfigs", ignoreCase = true)) {
                parsedResult = simpleSoapFormatter(
                        result[4].substring(
                                result[4].indexOf("<SOAP-ENV:Body>") + 15,
                                result[4].indexOf("</SOAP-ENV:Body>")))
                //				if (parsePtzConfigurationsResponse(result[4], selectedDevice.ptzConfigs)) {
                //					parsedResult = ptzConfigsToString(selectedDevice.ptzConfigs);
                //				}
            } else if (result[1].equals("getConfig", ignoreCase = true)) {
                parsedResult = simpleSoapFormatter(
                        result[4].substring(
                                result[4].indexOf("<SOAP-ENV:Body>") + 15,
                                result[4].indexOf("</SOAP-ENV:Body>")))
                //				if (parsePtzConfigurationResponse(result[4], selectedDevice.ptzConfig)) {
                //					parsedResult = ptzConfigToString(selectedDevice.ptzConfig);
                //				}
            } else if (result[1].equals("move", ignoreCase = true)) {
                parsedResult = ""
                ONVIFcommunication().execute(OnvifPtzStop.getStopCommand(selectedDevice.mediaProfiles[0].ptzNodeToken), "stopPTZ")
            } else if (result[1].equals("zoom", ignoreCase = true)) {
                ONVIFcommunication().execute(OnvifPtzStop.getStopCommand(selectedDevice.mediaProfiles[0].ptzNodeToken), "stopZ")
                parsedResult = ""
            } else if (result[1].equals("stopPTZ", ignoreCase = true)) {
                parsedResult = ""
            } else if (result[1].equals("stopZ", ignoreCase = true)) {
                parsedResult = ""
            }

            if (result[2].equals("init", ignoreCase = true)) {
                parsedResult = ""
                initComCnt += 1
                if (BuildConfig.DEBUG) Log.d(DEBUG_LOG_TAG, "Initialization success for " + result[0])
                if (initComCnt == 2) {
                    if (BuildConfig.DEBUG) Log.d(DEBUG_LOG_TAG, "Initialization success for 2 commands, start streaming")
                    rlUiId.setVisibility(View.VISIBLE)
                    //					tvStartup.setVisibility(View.GONE);

                    stopPlaybacks()
                    startRtspClientPlayer()
                    isPlaying = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        streamAction.setIcon(getDrawable(android.R.drawable.ic_media_pause))
                    } else {

                        streamAction.setIcon(getResources().getDrawable(android.R.drawable.ic_media_pause))
                    }
                }
            }
        }

        if (!parsedResult.isEmpty() && !result[2].equals("init", ignoreCase = true)) {
            val mySnackbar = Snackbar.make(findViewById<View>(android.R.id.content),
                    parsedResult,
                    Snackbar.LENGTH_INDEFINITE)
            mySnackbar.setAction("OK", mOnClickListener)
            val snackbarView = mySnackbar.getView()
            val tv = snackbarView.findViewById(android.support.design.R.id.snackbar_text) as TextView
            tv.maxLines = 300
            mySnackbar.show()
        }
    }
*/
}