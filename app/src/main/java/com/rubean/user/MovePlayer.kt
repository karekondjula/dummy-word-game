package com.rubean.user

data class MovePlayer(val word: String, val player: Player)

enum class Player { USER, BOT }

fun MovePlayer.playerWordToString() = if (player == Player.USER)
    "PL-1 : $word"
else
    "PL-2 : $word"