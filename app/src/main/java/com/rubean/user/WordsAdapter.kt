package com.rubean.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordsAdapter(private val listOfWords: MutableList<String>) :
    RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = listOfWords[position]
        holder.wordTextView.text = word
    }

    override fun getItemCount() = listOfWords.size

    fun addWord(newWord: String) {
        listOfWords.add(newWord)
        notifyItemInserted(listOfWords.size - 1)
    }
}