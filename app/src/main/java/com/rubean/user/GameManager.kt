package com.rubean.user

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface GameManager {
    val gameStateLiveData: LiveData<GameState>
    val serviceManager: ServiceManager

    fun userMove(move: String)
}

class GameManagerImpl(
    context: Context,
    lifecycleScope: LifecycleCoroutineScope
) : GameManager {

    private var lastMove: String? = null

    override val gameStateLiveData: MutableLiveData<GameState> = MutableLiveData()

    override val serviceManager: ServiceManager = ServiceManagerImpl(context)

    init {
        serviceManager.botMoveFlow.onEach { move ->
            delay(1500) // simulate short server delay
            analyzeMove(move, Player.BOT)
        }.launchIn(lifecycleScope)
    }

    private fun analyzeMove(move: String, player: Player) {
        val gameState = validateMove(move, player)

        gameStateLiveData.value = gameState

        lastMove = move

        if (gameState is GameState.ValidMove) {
            when (player) {
                Player.USER -> {
                    serviceManager.nextUserMove(move)
                    gameStateLiveData.value = GameState.BotTurn
                }
                Player.BOT -> {
                    gameStateLiveData.value = GameState.UserTurn
                }
            }
        }
    }

    override fun userMove(move: String) {
        analyzeMove(move, Player.USER)
    }

    private fun validateMove(move: String, player: Player): GameState {
        val listOfCurrentWords = lastMove?.split(" ")
        val newWords = move.split(" ")

        val wordPlayer = MovePlayer(move, player)

        when {
            newWords.contains("TOO_MUCH_FOR_ME") -> {
                return GameState.GameEnd(wordPlayer, "Bot is tired!")
            }
            newWords.size - (listOfCurrentWords?.size ?: 0) > 1 -> {
                return GameState.GameEnd(wordPlayer, "Only one new word is allowed!")
            }
            listOfCurrentWords?.contains(newWords.last()) ?: false -> {
                return GameState.GameEnd(wordPlayer, "Duplicate word: ${newWords.last()}")
            }
        }

        listOfCurrentWords?.forEachIndexed { i, word ->
            if (newWords[i] != word) {
                return GameState.GameEnd(
                    MovePlayer(move, player),
                    "Wrong word: ${newWords[i]}, correct is $word"
                )
            }
        }

        return GameState.ValidMove(wordPlayer)
    }
}

sealed class GameState {
    object UserTurn: GameState()
    object BotTurn: GameState()
    data class ValidMove(val movePlayer: MovePlayer) : GameState()
    data class GameEnd(val lastPlayed: MovePlayer, val reason: String) : GameState()
}