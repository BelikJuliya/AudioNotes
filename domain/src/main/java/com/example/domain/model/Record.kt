package com.example.domain.model

import com.example.domain.record.TrackStatus
import java.io.File

data class Record(
    val id: Int = 0,
    val title: String,
    val date: String,
    val audioLength: String,
    val playedLength: String = "",
    var isPlaying: Boolean = false,
    var status: TrackStatus = TrackStatus.Stopped,
    val mediaFile: File,
    var duration: Int = 0
)
