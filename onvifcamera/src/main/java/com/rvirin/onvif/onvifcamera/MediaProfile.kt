package com.rvirin.onvif.onvifcamera

/**
 * Created by Remy Virin on 05/03/2018.
 * @MediaProfile: is used to store an Onvif media profile (token and name)
 */
data class MediaProfile(val name: String, val token: String, val encoding: String) {
    fun canStream(): Boolean =
            encoding == "MPEG4" || encoding == "H264"

    fun canSnapshot(): Boolean =
            encoding == "JPEG"
}
