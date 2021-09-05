package com.rubean.user

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var newWordEditText: EditText? = null
    private var wordsAdapter: WordsAdapter? = null
    private var recyclerView: RecyclerView? = null

    private val gameManager: GameManager = GameManagerImpl(this, lifecycleScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initObservers()
        initRecyclerView()
        initEditText()
    }

    private fun initObservers() {
        lifecycle.addObserver(gameManager.serviceManager)

        gameManager.gameStateLiveData.observe(this, { gameState ->
            when (gameState) {
                GameState.UserTurn -> {
                    newWordEditText?.isEnabled = true
                    newWordEditText?.hint = getString(R.string.make_your_move)
                }
                GameState.BotTurn -> {
                    newWordEditText?.isEnabled = false
                    newWordEditText?.hint = getString(R.string.bots_turn)
                }
                is GameState.ValidMove -> {
                    addToListAndScrollToBottom(gameState.movePlayer.playerWordToString())
                }
                is GameState.GameEnd -> {
                    disableUI()
                    addToListAndScrollToBottom(gameState.lastPlayed.playerWordToString())
                    addToListAndScrollToBottom(gameState.reason)
                    showEndGameMessage(gameState)
                }
            }
        })
    }

    private fun showEndGameMessage(gameState: GameState.GameEnd) {
        val winner = if (gameState.lastPlayed.player == Player.USER) "BOT" else "USER"
        AlertDialog.Builder(this)
            .setTitle("Winner is $winner")
            .setMessage(gameState.reason)
            .create()
            .show()
    }

    private fun disableUI() {
        newWordEditText?.isEnabled = false
        recyclerView?.isEnabled = false
        findViewById<Button>(R.id.submit_button).isEnabled = false
    }

    private fun initRecyclerView() {
        wordsAdapter = WordsAdapter()
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

    private fun addToListAndScrollToBottom(word: String) {
        wordsAdapter?.let { nonNulAdapter ->
            nonNulAdapter.updateList(word)
            recyclerView?.smoothScrollToPosition(nonNulAdapter.itemCount)
        }
    }

    fun submitWord(view: View) {
        newWordEditText?.text.toString().let { move ->
            move.trim().let { trimmedWord ->
                if (trimmedWord.isNotEmpty()) {
                    gameManager.userMove(trimmedWord)
                    newWordEditText?.text?.clear()
                }
            }
        }
    }

    companion object {
        const val BOT_PACKAGE = "com.rubean.bot"
        const val BOT_ACTION = "$BOT_PACKAGE.start"
    }
}