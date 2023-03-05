package com.example.domain.record

sealed class TrackStatus {
    object Playing : TrackStatus()
    object Paused : TrackStatus()
    object Resumed : TrackStatus()
    object Stopped : TrackStatus()
}