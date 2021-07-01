package com.trendster.harpic.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trendster.api.models.Habit
import com.trendster.harpic.R
import com.trendster.harpic.databinding.ExploreRowLayoutBinding
import com.trendster.harpic.model.HabitItem
import com.trendster.harpic.ui.HabitDetailActivity
import com.trendster.harpic.util.HabitDiffUtil

class ExploreAdapter : RecyclerView.Adapter<ExploreAdapter.MyViewHolder>() {

    private var habits = emptyList<Habit>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = parent.context.getSystemService(LayoutInflater::class.java)
            .inflate(R.layout.explore_row_layout, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val list = habits[position]
        ExploreRowLayoutBinding.bind(holder.itemView).apply {
            txtHabitName.text = list.name
            val habitTimeline = list.duration
            txtTimeline.text = habitTimeline.toString()
            exploreCard.setOnClickListener {
                val intent = Intent(holder.itemView.context, HabitDetailActivity::class.java)
                intent.putExtra("Habit", list.toHabitItem())
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    fun setData(newData: List<Habit>) {
        val habitDiffUtil = HabitDiffUtil(habits, newData)
        val diffUtilResult = DiffUtil.calculateDiff(habitDiffUtil)
        habits = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun Habit.toHabitItem() = HabitItem(
        createdtime = createdtime,
        goal = goal,
        id = id,
        name = name,
        note = note,
        reward = reward,
        duration = duration,
        remaining = remaining
    )

}
