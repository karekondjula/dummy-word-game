package com.rubean.user

class GameManager(val listOfWords: MutableList<String>) {

    fun isMoveValid(move: String) : Boolean {
        return if (move.trim().isNotEmpty()) {
            // TODO check if move is valid
            true
        } else {
            false
        }
    }
}