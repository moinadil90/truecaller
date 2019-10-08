package com.truecaller.assignment.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.truecaller.assignment.R

import java.util.HashMap

class WordsAdapter(private var mContext: Context, private var modelList: HashMap<String, Int>) : RecyclerView.Adapter<WordsAdapter.WordsHolder>() {
    private val mKeys: Array<String> = modelList.keys.toTypedArray()

    inner class WordsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var txtWord: TextView = itemView.findViewById(R.id.txtWord)
        var txtCount: TextView = itemView.findViewById(R.id.txtCount)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item_words, parent, false)
        return WordsHolder(view)
    }

    override fun onBindViewHolder(holder: WordsHolder, position: Int) {

        //holder.txtWord.setText(modelList.get(position));

        holder.txtWord.text = mKeys[position]
        holder.txtCount.text = modelList[mKeys[position]].toString()
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

}
