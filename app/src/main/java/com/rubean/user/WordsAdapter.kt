package com.rubean.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordsAdapter(private val listOfMovePlayers: MutableList<String> = mutableListOf()) :
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
        val word = listOfMovePlayers[position]
        holder.wordTextView.text = word
    }

    override fun getItemCount() = listOfMovePlayers.size

    fun updateList(word: String) {
        listOfMovePlayers.add(word)
        notifyItemInserted(listOfMovePlayers.size - 1)
    }
}