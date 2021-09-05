package com.rubean.bot

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BotService : Service() {

    private val binder = BotBinder()

    lateinit var botCallback: IBotCallback

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "bot binding")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "bot starting")
        return START_STICKY
    }

    inner class BotBinder : IBotService.Stub() {
        override fun nextUserMove(move: String) {
            val botMove = "$move bot word"
            botCallback.nextBotMove(botMove)
        }

        override fun registerCallback(callback: IBotCallback) {
            botCallback = callback
        }
    }

    companion object {
        const val TAG = "BotService"
    }
}