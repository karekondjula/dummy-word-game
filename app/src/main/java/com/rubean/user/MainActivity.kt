package com.rubean.user

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rubean.bot.IBotCallback

class MainActivity : AppCompatActivity() {

    private var newWordEditText: EditText? = null
    private var wordsAdapter: WordsAdapter? = null
    private var recyclerView: RecyclerView? = null

    private val botCallback = object : IBotCallback.Stub() {
        override fun nextBotMove(move: String) {
            if (gameManager.isMoveValid(move)) {
                val playerMove = "PL-2 : $move"
                addMoveAndScrollToBottom(playerMove)
            }
        }
    }

    private var serviceManager = ServiceManager(this, botCallback)
    private val gameManager = GameManager(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycle.addObserver(serviceManager)

        initRecyclerView()

        initEditText()
    }

    private fun initRecyclerView() {
        wordsAdapter = WordsAdapter(mutableListOf())
        recyclerView = findViewById<RecyclerView>(R.id.words_recycler_view).apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = wordsAdapter
        }
    }

    private fun initEditText() {
        newWordEditText = findViewById<EditText>(R.id.new_word_edit_text).apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitWord(this@apply)
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun addMoveAndScrollToBottom(move: String) {
        wordsAdapter?.let { nonNulAdapter ->
            nonNulAdapter.addWord(move)
            recyclerView?.smoothScrollToPosition(nonNulAdapter.itemCount)
        }
    }

    fun submitWord(view: View) {
        newWordEditText?.text.toString().let { move ->
            if (gameManager.isMoveValid(move)) {
                addMoveAndScrollToBottom("PL-1 : $move")
                serviceManager.nextUserMove(move)
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
        const val BOT_PACKAGE = "com.rubean.bot"
        const val BOT_ACTION = "$BOT_PACKAGE.start"
    }
}