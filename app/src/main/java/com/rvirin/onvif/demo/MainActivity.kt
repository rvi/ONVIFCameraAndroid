package com.rvirin.onvif.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rvirin.onvif.R
import com.rvirin.onvif.onvifcamera.*
import com.rvirin.onvif.onvifcamera.OnvifRequest.Type.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val RTSP_URL = "com.rvirin.onvif.onvifcamera.demo.RTSP_URL"
const val JPEG_URL = "com.rvirin.onvif.onvifcamera.demo.JPEG_URL"
const val USERNAME = "com.rvirin.onvif.onvifcamera.demo.USERNAME"
const val PASSWORD = "com.rvirin.onvif.onvifcamera.demo.PASSWORD"

/**
 * Main activity of this demo project. It allows the user to type his camera IP address,
 * login and password.
 */
class MainActivity : AppCompatActivity(), OnvifListener {

    private var streamUri: String? = null
    private var snapshotUri: String? = null
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
        }
        // if GetServices have been completed, we request the device information
        else if (response.request.type == GetServices) {
            currentDevice.getDeviceInformation()
        }
        // if GetDeviceInformation have been completed, we request the profiles
        else if (response.request.type == GetDeviceInformation) {

            val textView = findViewById<TextView>(R.id.explanationTextView)
            textView.text = response.parsingUIMessage
            toast = Toast.makeText(this, "Device information retrieved 👍", Toast.LENGTH_SHORT)
            toast?.show()

            currentDevice.getProfiles()

        }
        // if GetProfiles have been completed, we request the Stream URI
        else if (response.request.type == GetProfiles) {
            val profilesCount = currentDevice.mediaProfiles.count()
            toast = Toast.makeText(this, "$profilesCount profiles retrieved 😎", Toast.LENGTH_SHORT)
            toast?.show()

            GlobalScope.launch(Dispatchers.Default) {
                currentDevice.getStreamURI()?.let {
                    runOnUiThread {
                        streamUri = it
                        val button = findViewById<TextView>(R.id.play_button)
                        button.isEnabled = true

                        toast = Toast.makeText(this@MainActivity, "Stream URI retrieved,\nready for the movie 🍿", Toast.LENGTH_SHORT)
                        toast?.show()
                    }
                }


                currentDevice.getSnapshotURI()?.let {
                    runOnUiThread {
                        snapshotUri = it
                        val button = findViewById<TextView>(R.id.view_button)
                        button.isEnabled = true

                        toast = Toast.makeText(this@MainActivity, "Snapshot URI retrieved,\nready for to view.🍿", Toast.LENGTH_SHORT)
                        toast?.show()
                    }
                }
            }
        }

    }

    fun connectClicked(view: View) {

        // get the information type by the user to create the Onvif device
        val ipAddress = (findViewById<EditText>(R.id.ipAddress)).text.toString()
        val login = (findViewById<EditText>(R.id.login)).text.toString()
        val password = (findViewById<EditText>(R.id.password)).text.toString()

        if (ipAddress.isNotEmpty() &&
                login.isNotEmpty() &&
                password.isNotEmpty()) {

            // Create ONVIF device with user inputs and retrieve camera informations
            currentDevice = OnvifDevice(ipAddress, login, password)
            currentDevice.listener = this
            currentDevice.getServices()

        } else {
            toast?.cancel()
            toast = Toast.makeText(this,
                    "Please enter an IP Address login and password",
                    Toast.LENGTH_SHORT)
            toast?.show()
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
                putExtra(USERNAME, currentDevice.username)
                putExtra(PASSWORD, currentDevice.password)
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "JPEG URI hasn't been retrieved", Toast.LENGTH_LONG).show()
        }
    }
}
