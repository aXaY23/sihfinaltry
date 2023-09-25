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

    private lateinit var awdeo:MediaPlayer
    private lateinit val hand: Handler()
    private lateinit var V: Visualizer
    private lateinit var VolData: TextView



    private var pause:Boolean = false
    var volumeC = getSystemService(Context.AUDIO_SERVICE) as AudioManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        val play = findViewById<Button>(R.id.play)
        val volumeSeekBar = findViewById<SeekBar>(R.id.seekBar)
        val Rec = findViewById<Button>(R.id.Rec)

        play.setOnClickListener {
            if (pause) {
                awdeo.seekTo(awdeo.currentPosition)
                awdeo.start()
                pause = false
                play.text = "Stop"
                Repeat()
                sVir()
            } else {

                awdeo = MediaPlayer.create(applicationContext, R.raw.fhz)
                awdeo.start()
                play.text = "PLAY"
                V.release()

            }
        }

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                val maxVolume = volumeC.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volume = progress.toFloat() / 100 * maxVolume
                volumeC.setStreamVolume(AudioManager.STREAM_MUSIC, volume.toInt(), 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        Rec.setOnClickListener{
            val Data: Double  // @TuhinChatterjee volume ka data store karna array form me. its supposed to be arry for data collection //

        }
    }  //@tuhinChatterjee something for you tu cover up here
    private fun Repeat() {
        val delayMillis = awdeo.duration.toLong()

        hand.postDelayed(object : Runnable {
            override fun run() {
                if (awdeo.isPlaying) {
                    awdeo.seekTo(0)
                    awdeo.start()
                    Repeat()
                }
            }
        }, delayMillis)
    }

    private fun DBvolume(waveform: ByteArray?): Double {
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
    private fun sVir() {
        V.captureSize = Visualizer.getCaptureSizeRange()[1]
        V.setDataCaptureListener(
            object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(
                    visualizer: Visualizer?,
                    waveform: ByteArray?,
                    samplingRate: Int
                ) {
                    val volume = DBvolume(waveform)
                    VolData.text = "Volume: ${volume} dB"
                }

                override fun onFftDataCapture(
                    visualizer: Visualizer?,
                    fft: ByteArray?,
                    samplingRate: Int
                ) {}
            },
            Visualizer.getMaxCaptureRate(),
            false,
            true
        )
        V.enabled = true
    }
    }



