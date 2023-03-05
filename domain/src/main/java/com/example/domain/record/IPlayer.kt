package com.example.domain.record


interface IPlayer {

    suspend fun play(
        uriString: String,
        onComplete: () -> Unit,
        onProgressChanged: (progress: Int) -> Unit,
        setDuration: (duration: Int) -> Unit
    )

    suspend fun resume(uriString: String, onProgressChanged: (progress: Int) -> Unit)

    fun pause(uriString: String)

    fun stop(uriString: String)
}