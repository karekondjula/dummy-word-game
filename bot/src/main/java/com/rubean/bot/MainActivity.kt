package com.rubean.bot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(BOT_ACTION).also { intent ->
            intent.setPackage(BOT_PACKAGE)
            startService(intent)
        }
        finish()
    }

    companion object {
        const val BOT_PACKAGE = "com.rubean.bot"
        const val BOT_ACTION = "$BOT_PACKAGE.start"
    }
}