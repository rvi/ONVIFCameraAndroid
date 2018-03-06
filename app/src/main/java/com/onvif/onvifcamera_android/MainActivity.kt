package com.onvif.onvifcamera_android

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.onvif.onvifcamera_android.Onvif.OnvifDevice
import com.onvif.onvifcamera_android.Onvif.OnvifRequest
import com.onvif.onvifcamera_android.Onvif.OnvifUI
import com.onvif.onvifcamera_android.Onvif.currentDevice

const val RTSP_URL = "com.onvif.onvifcamera_android.RTSP_URL"

class MainActivity : AppCompatActivity(), OnvifUI {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //currentDevice = OnvifDevice("60.191.94.122:8086","admin", "admin321")

        currentDevice = OnvifDevice("60.191.94.122:8086", "admin", "admin321")
        currentDevice.delegate = this

        // currentDevice.getDeviceInformation()
        currentDevice.getProfiles()
        // currentDevice.getStreamURI()
    }

    override fun requestPerformed(requestType: OnvifRequest.Type, uiMessage: String) {

        Log.d("INFO", uiMessage)
        if (requestType == OnvifRequest.Type.GetDeviceInformation) {
            val textView = findViewById<TextView>(R.id.explanationTextView)
            textView.text = uiMessage
        } else if (requestType == OnvifRequest.Type.GetProfiles) {
            currentDevice.getStreamURI()
        }
    }


    fun openStreamIntent(view: View) {

        currentDevice.rtspURI?.let { uri ->
            val intent = Intent(this, StreamActivity::class.java).apply {
                putExtra(RTSP_URL, uri)
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "RTSP URI haven't been retrieved", Toast.LENGTH_SHORT).show()
        }
    }
}
