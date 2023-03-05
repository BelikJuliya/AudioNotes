package com.example.audionotes.ui.records

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Record
import com.example.domain.usecase.LoadRecordsUseCase
import com.example.domain.usecase.PlayAudioUseCase
import com.example.domain.usecase.RecordAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(
    private val recordAudioUseCase: RecordAudioUseCase,
    private val loadRecordsUseCase: LoadRecordsUseCase,
    private val playAudioUseCase: PlayAudioUseCase
) : ViewModel() {
    private lateinit var audioFile: File
    private var audioLength: String = "00:00"
    val recordsLiveData = MutableLiveData<List<Record>>()
    private var recordingTime: Long = 0
    private var timer = Timer()
    val recordingTimeLiveData = MutableLiveData<String>()
    val errorLiveData = MutableLiveData<String>()

    fun initRecorder() {
        recordAudioUseCase.initRecorder()
    }

    fun getRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            recordsLiveData.postValue(loadRecordsUseCase.loadRecordsFromDb())
        }
    }

    fun recordAudio() {
        audioFile =
            File(Environment.getExternalStorageDirectory().absolutePath
                    + "/soundrecorder/recording" + Math.random() + ".mp3")
        recordAudioUseCase.startRecording(audioFile, error = {
            errorLiveData.postValue(it)
        })
        startTimer()
    }

    fun stopRecording() {
        recordAudioUseCase.stopRecording()
        stopTimer()
    }

    fun saveRecord(title: String) {
        val items = mutableListOf<Record>()
        recordsLiveData.value?.let { items.addAll(it) }
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        viewModelScope.launch(Dispatchers.IO) {
            val record = Record(
                title = title,
                date = formatter.format(Date()),
                audioLength = audioLength,
                mediaFile = audioFile
            )
            loadRecordsUseCase.saveRecord(record)
            items.add(0, record)
            recordsLiveData.postValue(items)
        }
    }

    private fun startTimer() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                recordingTime += 1
                updateDisplay()
            }
        }, 1000, 1000)
    }

    private fun stopTimer() {
        timer.cancel()
    }

    fun resetTimer() {
        timer.cancel()
        recordingTime = 0
        recordingTimeLiveData.postValue("00:00")
    }

    private fun updateDisplay() {
        val minutes = recordingTime / (60)
        val seconds = recordingTime % 60
        audioLength = String.format("%d:%02d", minutes, seconds)
        recordingTimeLiveData.postValue(audioLength)
    }

    fun play(
        file: File,
        onComplete: () -> Unit,
        onProgressChanged: (progress: Int) -> Unit,
        setDuration: (duration: Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playAudioUseCase.play(
                    uriString = file.absolutePath,
                    onComplete = onComplete,
                    onProgressChanged = onProgressChanged,
                    setDuration = setDuration
                )
            } catch (ex: java.lang.Exception) {
                errorLiveData.postValue(ex.message)
            }
        }
    }

    fun pause(file: File) {
        playAudioUseCase.pause(file.absolutePath)
    }

    fun resume(file: File, onProgressChanged: (progress: Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            playAudioUseCase.resume(file.absolutePath, onProgressChanged)
        }
    }

    fun stop(file: File) {
        playAudioUseCase.stop(file.absolutePath)
    }
}