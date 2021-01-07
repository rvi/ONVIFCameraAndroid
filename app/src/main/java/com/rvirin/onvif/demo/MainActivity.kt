package com.rvirin.onvif.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rvirin.onvif.R
import com.rvirin.onvif.onvifcamera.*
import kotlinx.coroutines.launch

const val RTSP_URL = "com.rvirin.onvif.onvifcamera.demo.RTSP_URL"
const val JPEG_URL = "com.rvirin.onvif.onvifcamera.demo.JPEG_URL"
const val USERNAME = "com.rvirin.onvif.onvifcamera.demo.USERNAME"
const val PASSWORD = "com.rvirin.onvif.onvifcamera.demo.PASSWORD"

/**
 * Main activity of this demo project. It allows the user to type his camera IP address,
 * login and password.
 */
class MainActivity : AppCompatActivity() {

    private var streamUri: String? = null
    private var snapshotUri: String? = null
    private var device: OnvifDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun connectClicked(view: View) {

        // get the information type by the user to create the Onvif device
        val ipAddress = (findViewById<EditText>(R.id.ipAddress)).text.toString()
        val login = (findViewById<EditText>(R.id.login)).text.toString()
        val password = (findViewById<EditText>(R.id.password)).text.toString()

        if (ipAddress.isNotEmpty()) {
            lifecycleScope.launch {
                // Get camera services
                val device = OnvifDevice.requestDevice(ipAddress, login, password)
                this@MainActivity.device = device
                
                // Display camera specs
                val deviceInformation = device.getDeviceInformation()
                val textView = findViewById<TextView>(R.id.explanationTextView)
                textView.text = deviceInformation.toString()
                
                // Get media profiles to find which ones are streams/snapshots
                val profiles = device.getProfiles()
                
                profiles.firstOrNull { it.canStream() }?.let {
                    device.getStreamURI(it, addCredentials = true)?.let { uri ->
                        runOnUiThread {
                            streamUri = uri
                            val button = findViewById<TextView>(R.id.play_button)
                            button.isEnabled = true
                        }
                    }
                }
                
                profiles.firstOrNull { it.canSnapshot() }?.let { 
                    device.getSnapshotURI(it)?.let { uri ->
                        runOnUiThread {
                            snapshotUri = uri
                            val button = findViewById<TextView>(R.id.view_button)
                            button.isEnabled = true
                        }
                    }
                }
                
            }
        } else {
            Toast.makeText(this,
                    "Please enter an IP Address login and password",
                    Toast.LENGTH_SHORT).show()
        }
    }

    fun playClicked(view: View) {
        // If we were able to retrieve information from the camera, and if we have a rtsp uri,
        // We open StreamActivity and pass the rtsp URI
        streamUri?.let { uri ->
            val intent = Intent(this, StreamActivity::class.java).apply {
                putExtra(RTSP_URL, uri)
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "RTSP URI haven't been retrieved", Toast.LENGTH_SHORT).show()
        }
    }

    fun viewClicked(view: View) {
        snapshotUri?.let { uri ->
            val intent = Intent(this, SnapshotActivity::class.java).apply {
                putExtra(JPEG_URL, uri)
                putExtra(USERNAME, device?.username)
                putExtra(PASSWORD, device?.password)
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "JPEG URI hasn't been retrieved", Toast.LENGTH_LONG).show()
        }
    }
}
