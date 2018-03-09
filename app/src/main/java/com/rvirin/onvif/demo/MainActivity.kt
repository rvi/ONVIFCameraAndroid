package com.rvirin.onvif.demo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.rvirin.onvif.R

import com.rvirin.onvif.onvifcamera.OnvifRequest.Type.GetStreamURI
import com.rvirin.onvif.onvifcamera.OnvifRequest.Type.GetProfiles
import com.rvirin.onvif.onvifcamera.OnvifRequest.Type.GetDeviceInformation
import com.rvirin.onvif.onvifcamera.OnvifResponse
import com.rvirin.onvif.onvifcamera.OnvifListener
import com.rvirin.onvif.onvifcamera.OnvifDevice
import com.rvirin.onvif.onvifcamera.currentDevice

const val RTSP_URL = "com.rvirin.onvif.onvifcamera.demo.RTSP_URL"

class MainActivity : AppCompatActivity(), OnvifListener {

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun requestPerformed(response: OnvifResponse) {

        Log.d("INFO", response.parsingUIMessage)

        toast?.cancel()

        if (!response.success) {
            Log.e("ERROR", "request failed: ${response.request.type} \n Response: ${response.error}")
            toast = Toast.makeText(this, "⛔️ Request failed: ${response.request.type}", Toast.LENGTH_SHORT)
            toast?.show()
        } else if (response.request.type == GetDeviceInformation) {

            val textView = findViewById<TextView>(R.id.explanationTextView)
            textView.text = response.parsingUIMessage
            toast = Toast.makeText(this, "Device information retrieved 👍", Toast.LENGTH_SHORT)
            toast?.show()

            currentDevice.getProfiles()

        } else if (response.request.type == GetProfiles) {
            val profilesCount = currentDevice.mediaProfiles.count()
            toast = Toast.makeText(this, "$profilesCount profiles retrieved 😎", Toast.LENGTH_SHORT)
            toast?.show()

            currentDevice.getStreamURI()

        } else if (response.request.type == GetStreamURI) {

            val button = findViewById<TextView>(R.id.button)
            button.text = getString(R.string.Play)

            toast = Toast.makeText(this, "Stream URI retrieved,\nready for the movie 🍿", Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    fun buttonClicked(view: View) {

        if (currentDevice.isConnected) {
            currentDevice.rtspURI?.let { uri ->
                val intent = Intent(this, StreamActivity::class.java).apply {
                    putExtra(RTSP_URL, uri)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "RTSP URI haven't been retrieved", Toast.LENGTH_SHORT).show()
            }
        } else {

            // get the information type by the user and create the Onvif device
            val ipAddress = (findViewById<EditText>(R.id.ipAddress)).text.toString()
            val login = (findViewById<EditText>(R.id.login)).text.toString()
            val password = (findViewById<EditText>(R.id.password)).text.toString()

            if (ipAddress.isNotEmpty() &&
                    login.isNotEmpty() &&
                    password.isNotEmpty()) {

                currentDevice = OnvifDevice(ipAddress, login, password)
                currentDevice.listener = this
                currentDevice.getDeviceInformation()
            } else {
                toast?.cancel()
                toast = Toast.makeText(this,
                        "Please enter an IP Address login and password",
                        Toast.LENGTH_SHORT)
                toast?.show()
            }
        }
    }
}
