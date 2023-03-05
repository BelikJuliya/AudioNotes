package com.example.data.record.record

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import com.example.domain.record.IPlayer
import kotlinx.coroutines.delay
import java.io.File


class PlayerRepositoryImpl(
    val context: Context
) : IPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var resumePosition = 0

    override suspend fun play(
        uriString: String,
        onComplete: () -> Unit,
        onProgressChanged: (progress: Int) -> Unit,
        setDuration: (duration: Int) -> Unit,
    ) {
        MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(context, Uri.fromFile(File(uriString)))
            prepare()
            start()
            setOnCompletionListener {
                onComplete()
                onProgressChanged( 0)
            }
        }.also { mediaPlayer = it }
        setDuration(mediaPlayer?.duration ?: 0)
        observeProgress(onProgressChanged)
    }

    override fun stop(uriString: String) {
        stopPlayer()
    }

    private fun stopPlayer() {
        mediaPlayer?.let {
            it.release()
            mediaPlayer = null
        }
    }

    override  fun pause(uriString: String) {
        mediaPlayer?.pause()
        resumePosition = mediaPlayer?.currentPosition ?: 0
    }

    override suspend fun resume(uriString: String, onProgressChanged: (progress: Int) -> Unit) {
        mediaPlayer?.let {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.seekTo(resumePosition)
                mediaPlayer?.start()
            }
        }
        observeProgress(onProgressChanged)
    }

    private suspend fun observeProgress(onProgressChanged: (progress: Int) -> Unit) {
        while (mediaPlayer?.isPlaying == true) {
            onProgressChanged(mediaPlayer?.currentPosition ?: 0)
            delay(300)
        }
    }
}




