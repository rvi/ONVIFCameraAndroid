package com.rvirin.onvif.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.burgstaller.okhttp.DispatchingAuthenticator
import com.burgstaller.okhttp.basic.BasicAuthenticator
import com.burgstaller.okhttp.digest.Credentials
import com.burgstaller.okhttp.digest.DigestAuthenticator
import com.rvirin.onvif.R
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class SnapshotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapshot)

        val imageView = findViewById<ImageView>(R.id.image_view)

        val username = intent.getStringExtra(USERNAME)
        val password = intent.getStringExtra(PASSWORD)
        val url = intent.getStringExtra(JPEG_URL)!!

        val credentials =  Credentials(username, password)
        val authenticator = DispatchingAuthenticator.Builder()
                .with("digest", DigestAuthenticator(credentials))
                .with("basic", BasicAuthenticator(credentials))
                .build()
        val client = OkHttpClient.Builder()
                .authenticator(authenticator)
                .connectTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(10000, TimeUnit.SECONDS)
                .build()

        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SnapshotActivity", "Image download failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val bitmap = BitmapFactory.decodeStream(response.body!!.byteStream())
                    runOnUiThread {
                        imageView.setImageBitmap(bitmap)
                    }
                } else {
                    Toast.makeText(this@SnapshotActivity, "${response.code} - ${response.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}