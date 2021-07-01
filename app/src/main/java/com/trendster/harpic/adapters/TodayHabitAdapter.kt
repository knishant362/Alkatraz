package com.trendster.harpic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trendster.api.models.DateFormat
import com.trendster.api.models.SelectedHabits
import com.trendster.harpic.R
import com.trendster.harpic.databinding.HabitRowLayoutBinding
import com.trendster.harpic.util.*
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

class TodayHabitAdapter(val onButtonClick: (HashMap<String, Any>) -> Unit) : RecyclerView.Adapter<TodayHabitAdapter.MyViewHolder>() {

    private var habitsList = emptyList<SelectedHabits>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = parent.context.getSystemService(LayoutInflater::class.java)
            .inflate(R.layout.habit_row_layout, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val habit = habitsList[position]
        HabitRowLayoutBinding.bind(holder.itemView).apply {

            val habitTimeline = "${habit.remaining} Days"
            txtHabitTitle.text = habit.name
            txtRemaining.text = habitTimeline

            val today = todayDay()
            val dayOfHabit = (habit.startDate.date - today.date)
            llButton.visibility = checkVisibility(habit.days, dayOfHabit)

            btnDone.setOnClickListener {
                val newDataMap = createDataMap(
                    habit.name,
                    tomorrowDate(),
                    habit.remaining - 1,
                    newDaysAttendance(habit.days, dayOfHabit, 'D'),
                    habitStatus(habit.days, dayOfHabit)
                )
                onButtonClick(newDataMap)
            }
            btnForgot.setOnClickListener {
                val newDataMap = createDataMap(
                    habit.name,
                    tomorrowDate(),
                    habit.remaining - 1,
                    newDaysAttendance(habit.days, dayOfHabit, 'F'),
                    habitStatus(habit.days, dayOfHabit)
                )
                onButtonClick(newDataMap)
            }
        }
    }

    private fun checkVisibility(days: String, dayOfHabit: Int): Int {
        return when (days[dayOfHabit].toString()) {
            "D" -> {
                View.GONE
            }
            "F" -> {
                View.GONE
            }
            "0" -> {
                View.VISIBLE
            }
            else -> {
                View.VISIBLE
            }
        }
    }

    private fun createDataMap(
        name: String,
        tomorrowDate: DateFormat,
        remaining: Int,
        newDaysAttendance: String,
        habitStatus: String
    ): HashMap<String, Any> {
        val habitData = HashMap<String, Any>()
        habitData[NAME] = name
        habitData[NEXT_DATE] = tomorrowDate
        habitData[REMAINING] = remaining
        habitData[DAYS] = newDaysAttendance
        habitData[DAY_OF_HABIT] = habitStatus
        return habitData
    }

    private fun newDaysAttendance(days: String, dayOfHabit: Int, attendance: Char): String {
        val sb = StringBuilder(days).also {
            it.setCharAt(dayOfHabit, attendance)
        }
        return sb.toString()
    }

    private fun habitStatus(days: String, dayOfHabit: Int): String {
        return when (days[dayOfHabit].toString()) {
            "D" -> {
                "done"
            }
            "F" -> {
                "forget"
            }
            "0" -> {
                "pending"
            }
            else -> {
                "Error Biro"
            }
        }
    }

    private fun todayDay(): DateFormat {
        val today = Calendar.getInstance()

        /**individual item */
        val myday = today.get(Calendar.DATE)
        val month = today.get(Calendar.MONTH)
        val year = today.get(Calendar.YEAR)

        return DateFormat(myday, month + 1, year)
    }

    private fun tomorrowDate(): DateFormat {
        val calender = Calendar.getInstance()
        calender.add(Calendar.DAY_OF_YEAR, 1)

        /**individual item */
        val myday = calender.get(Calendar.DATE)
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)

        return DateFormat(myday, month + 1, year)
    }

    override fun getItemCount(): Int {
        return habitsList.size
    }

    fun setData(newData: List<SelectedHabits>) {
        val habitDiffUtil = HabitDiffUtil(habitsList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(habitDiffUtil)
        habitsList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
