package com.rubean.user

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rubean.bot.IBotCallback
import com.rubean.bot.IBotService

class MainActivity : AppCompatActivity() {

    private var newWordEditText: EditText? = null
    private var wordsAdapter: WordsAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var botService: IBotService? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            botService = IBotService.Stub.asInterface(binder)

            try {
                botService?.registerCallback(botCallback)
            } catch (e: RemoteException) {
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
        }
    }

    private val botCallback = object : IBotCallback.Stub() {
        override fun nextBotMove(words: List<String>) {
            // TODO check bot move
            // TODO if OK add word to adapter
            addWordAndScrollToBottom(words.last())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newWordEditText = findViewById<EditText>(R.id.new_word_edit_text).apply {
            setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    return if (actionId == EditorInfo.IME_ACTION_DONE) {
                        submitWord(this@apply)
                        true
                    } else {
                        false
                    }
                }
            })
        }

        wordsAdapter = WordsAdapter(mutableListOf())
        recyclerView = findViewById<RecyclerView>(R.id.words_recycler_view).apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = wordsAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(BOT_ACTION).also { intent ->
            intent.setPackage(BOT_PACKAGE)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    fun submitWord(view: View) {
        newWordEditText?.text.toString().let { word ->
            // TODO check user move
            // TODO keep track a list of all played words
            addWordAndScrollToBottom(word)
            botService?.nextUserMove(arrayListOf<String>().also {
                it.add(word)
            })
        }
    }

    private fun addWordAndScrollToBottom(word: String) {
        wordsAdapter?.let { nonNulAdapter ->
            if (word.trim().isNotEmpty()) {
                nonNulAdapter.addWord(word)
                recyclerView?.smoothScrollToPosition(nonNulAdapter.itemCount)
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
        const val BOT_PACKAGE = "com.rubean.bot"
        const val BOT_ACTION = "$BOT_PACKAGE.start"
    }
}