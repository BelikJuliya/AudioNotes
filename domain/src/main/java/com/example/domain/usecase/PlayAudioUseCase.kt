package com.example.domain.usecase

import com.example.domain.record.IPlayer

class PlayAudioUseCase(
    private val player: IPlayer

) {
    suspend fun play(uriString: String, onComplete: () -> Unit, onProgressChanged: (progress: Int) -> Unit, setDuration: (duration: Int) -> Unit) {
        player.play(uriString, onComplete, onProgressChanged, setDuration)
    }

    fun pause(uriString: String) {
        player.pause(uriString)
    }

    suspend fun resume(uriString: String, onProgressChanged: (progress: Int) -> Unit) {
        player.resume(uriString, onProgressChanged)
    }

    fun stop(uriString: String) {
        player.stop(uriString)
    }
}