package com.example.sihfinaltry

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Context

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var visualizer: Visualizer
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumeTextView: TextView
    private lateinit var audioManager: AudioManager
    private val audioResource = R.raw.fhz // Replace with your audio file name
    private val handler = Handler()

    private fun calculateVolumeInDecibels(waveform: ByteArray?): Double {
         if (waveform != null) {
             var sum = 0.0
             for (i in waveform) {
                            sum += i.toDouble()
                        }
             val rms = Math.sqrt(sum / waveform.size)
             val db = 20 * Math.log10(rms)
             return db
         }
                    return 0.0
                }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, audioResource)
        visualizer = Visualizer(mediaPlayer.audioSessionId)
        volumeSeekBar = findViewById(R.id.seekBar)
        volumeTextView = findViewById(R.id.dbVolume)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val playButton = findViewById<Button>(R.id.play)
        playButton.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                playButton.text = "Stop Audio"
                startAudioRepeat()
                setupVisualizer()
            } else {
                mediaPlayer.stop()
                mediaPlayer.prepare()
                playButton.text = "Play Audio"
                visualizer.release()
            }
        }

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Adjust audio volume based on SeekBar progress
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volume = progress.toFloat() / 100 * maxVolume
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume.toInt(), 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startAudioRepeat() {
        val delayMillis = mediaPlayer.duration.toLong()

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                    startAudioRepeat()
                }
            }
        }, delayMillis)
    }
    private fun setupVisualizer() {
        visualizer.captureSize = Visualizer.getCaptureSizeRange()[1]
        visualizer.setDataCaptureListener(
            object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(
                    visualizer: Visualizer?,
                    waveform: ByteArray?,
                    samplingRate: Int
                ) {
                    val volume = calculateVolumeInDecibels(waveform)
                    volumeTextView.text = "Volume: ${volume} dB"
                }

                override fun onFftDataCapture(
                    visualizer: Visualizer?,
                    fft: ByteArray?,
                    samplingRate: Int
                ) {
                    // Not needed for displaying volume in dB
                }
            },
            Visualizer.getMaxCaptureRate(),
            false,
            true
        )
        visualizer.enabled = true
    }
}