package com.example.domain.record

import java.io.File

interface IRecorderRepository {

    fun initRecorder()

    fun startRecording(file: File, error: (String) -> Unit)

    fun stopRecording()

}