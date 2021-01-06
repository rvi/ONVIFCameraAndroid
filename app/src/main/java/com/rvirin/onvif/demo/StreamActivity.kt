package com.rvirin.onvif.demo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rvirin.onvif.R
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

/**
 * This activity helps us to show the live stream of an ONVIF camera thanks to VLC library.
 */
class StreamActivity : AppCompatActivity() {

    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream)

        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)
    }

    override fun onStart() {
        super.onStart()

        val videoView = findViewById<VLCVideoLayout>(R.id.video_view)
        mediaPlayer.attachViews(videoView, null, false, false)

        val url = intent.getStringExtra(RTSP_URL)
        val uri = Uri.parse(url)
        val media = Media(libVLC, uri)
        mediaPlayer.media = media
        media.release()
        mediaPlayer.play()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.release()
        libVLC.release()
    }

    override fun onStop() {
        super.onStop()

        mediaPlayer.stop()
        mediaPlayer.detachViews()
    }
}

