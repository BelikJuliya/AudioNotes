package com.example.domain.usecase

import com.example.domain.record.IRecorderRepository
import java.io.File

class RecordAudioUseCase(
    private val recorder: IRecorderRepository
) {

    fun initRecorder() {
        recorder.initRecorder()
    }

    fun startRecording(file: File, error: (String) -> Unit) {
        return recorder.startRecording(file, error)
    }

    fun stopRecording() {
        recorder.stopRecording()
    }
}