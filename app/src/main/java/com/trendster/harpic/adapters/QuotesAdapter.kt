package com.trendster.harpic.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trendster.api.models.Habit
import com.trendster.api.models.Quotes
import com.trendster.harpic.R
import com.trendster.harpic.databinding.ExploreRowLayoutBinding
import com.trendster.harpic.databinding.QuotesRowLayoutBinding
import com.trendster.harpic.model.HabitItem
import com.trendster.harpic.ui.HabitDetailActivity
import com.trendster.harpic.util.HabitDiffUtil

class QuotesAdapter : RecyclerView.Adapter<QuotesAdapter.MyViewHolder>() {

    private var quotes = emptyList<Quotes>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = parent.context.getSystemService(LayoutInflater::class.java)
            .inflate(R.layout.quotes_row_layout, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val list = quotes[position]
        QuotesRowLayoutBinding.bind(holder.itemView).apply {
            txtAuthorName.text = list.author
            txtQuoteStatement.text = list.statement
        }
    }

    override fun getItemCount(): Int {
        return quotes.size
    }

    fun setData(newData: List<Quotes>) {
        val habitDiffUtil = HabitDiffUtil(quotes, newData)
        val diffUtilResult = DiffUtil.calculateDiff(habitDiffUtil)
        quotes = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

}
