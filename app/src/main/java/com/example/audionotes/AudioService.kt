package com.example.audionotes

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.IOException


// Не хватило времени доделать сервис, пришлось выбрать более простую реализацию, но сервси уже частично работает
// Но я понимаю, что плеер должен быть сервисом, чтобы уметь проигрывать треки, когда приложение находится в бекграунде

class AudioService : Service(), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
{
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioManager: AudioManager
    var mBinder: IBinder = LocalBinder()
    private var resumePosition = 0

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Example Service")
            .setContentText(input)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    class LocalBinder : Binder() {
        fun getServerInstance(): AudioService {
            return AudioService()
        }
    }

    private fun initMediaPlayer(mediaFile: File) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.reset()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.prepareAsync()
    }

    fun playMedia(mediaFile: File) {
        initMediaPlayer(mediaFile)
        try {
            mediaPlayer?.setDataSource(mediaFile.path)
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
    }

    fun pauseMedia(file: File) {
        try {
            mediaPlayer?.setDataSource(file.path)
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            resumePosition = mediaPlayer?.currentPosition ?: 0
        }
    }

     fun resumeMedia(file: File) {
         try {
             mediaPlayer?.setDataSource(file.path)
         } catch (e: IOException) {
             e.printStackTrace()
             stopSelf()
         }
        if (!mediaPlayer?.isPlaying!!) {
            mediaPlayer?.seekTo(resumePosition)
            mediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMedia()
        mediaPlayer?.release()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        //Invoked when playback of a media source has completed.
        stopMedia()
        //stop the service
        stopSelf()
    }

    //Handle errors
    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        //Invoked when there has been an error during an asynchronous operation
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra"
            )
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR SERVER DIED $extra"
            )
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR UNKNOWN $extra"
            )
        }
        return false
    }

    override fun onPrepared(mp: MediaPlayer?) {
        //Invoked when the media source is ready for playback.
        //playMedia()
    }
}