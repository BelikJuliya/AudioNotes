package com.example.data.record.record

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.domain.record.IRecorderRepository
import java.io.IOException
import java.io.File
import java.io.FileOutputStream
import java.util.*

class RecorderRepositoryImpl(
    val context: Context
) : IRecorderRepository {
    private  var mediaRecorder: MediaRecorder? = null

    override fun initRecorder() {
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        }
        try {
            // create directory for records
            File(Environment.getExternalStorageDirectory().absolutePath + "/soundrecorder/").mkdirs()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun startRecording(file: File, error: (String) -> Unit) {
        if (mediaRecorder != null) {
            mediaRecorder?.setOutputFile(FileOutputStream(file).fd)
            try {
                mediaRecorder?.prepare()
                mediaRecorder?.start()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                e.message?.let { error(it) }
            } catch (e: IOException) {
                e.printStackTrace()
                e.message?.let { error(it) }
            }
        } else {
            initRecorder()
            startRecording(file, error)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (ex: java.lang.Exception) {
            Log.e(this.javaClass.simpleName, "stopRecording: ", ex)
        }
    }
}