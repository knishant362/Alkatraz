package com.trendster.harpic.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.db.williamchart.data.AxisType
import com.google.firebase.auth.FirebaseAuth
import com.trendster.api.models.DateFormat
import com.trendster.api.models.SelectedHabits
import com.trendster.harpic.R
import com.trendster.harpic.adapters.TodayHabitAdapter
import com.trendster.harpic.databinding.FragmentDashboardBinding
import com.trendster.harpic.util.*
import com.trendster.harpic.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var mAdapter: TodayHabitAdapter
    private var monday = 1
    private var tuesday = 1
    private var wednesday = 1
    private var thrusday = 1
    private var friday = 1
    private var saturday = 1
    private var sunday = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        mAdapter = TodayHabitAdapter { newData ->
            if (auth.currentUser?.uid != null)
                mainViewModel.markTodayAttendance(auth.currentUser!!.uid, newData)
        }
        setupRecycler()

        setupChart()

        if (auth.currentUser?.uid != null)
            mainViewModel.fetchUserHabits(auth.currentUser!!.uid)

        mainViewModel.userHabitsResponse.observe(
            viewLifecycleOwner,
            { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        showShimmer()
                    }
                    is NetworkResult.Success -> {
                        if (response.data?.isEmpty() == true) {
                            placeHolderVisibility(View.VISIBLE)
                            hideShimmer()
                        } else {
                            response.data?.let { mAdapter.setData(it) }
                            val weekHabits = checkThisWeeksHabits(response.data)
                            chartDataSetup(weekHabits)
                            mAdapter.notifyDataSetChanged()
                            hideShimmer()
                        }
                    }
                    is NetworkResult.Error -> {
                        showToast(response.message.toString())
                        placeHolderVisibility(View.VISIBLE)
                        hideShimmer()
                    }
                }
            }
        )

        mainViewModel.attendanceResponse.observe(
            viewLifecycleOwner,
            { response ->

                when (response) {
                    is NetworkResult.Loading -> {
                        showShimmer()
                    }
                    is NetworkResult.Success -> {
                        mainViewModel.fetchUserHabits(auth.currentUser!!.uid)
                    }
                    is NetworkResult.Error -> {
                        hideShimmer()
                        showToast(response.message.toString())
                    }
                }
            }
        )

        binding.imgDrawer.setOnClickListener {
            val drawerLayout: DrawerLayout? = activity?.findViewById(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }

        return binding.root
    }

    private fun showShimmer() {
        binding.todayHabitRecycler.showShimmer()
    }

    private fun hideShimmer() {
        binding.todayHabitRecycler.hideShimmer()
    }

    private fun chartDataSetup(weekHabits: MutableList<SelectedHabits>) {
        for (habit in weekHabits) {
            checkWeekAttendance(habit)
        }
    }

    private fun checkWeekAttendance(habit: SelectedHabits) {
        val days = habit.days
        val startDate = habit.startDate
        val todayDate = todayDate()


    }

    private fun findMondayDate(todayDate: Pair<DateFormat, String>): DateFormat {
        return mondayDateFinder(minusDaysToMonday(todayDate.second))
    }

    private fun mondayDateFinder(value: Int): DateFormat {
        val calender = Calendar.getInstance()
        calender.add(Calendar.DAY_OF_YEAR, value)

        /**individual item */
        val myday = calender.get(Calendar.DATE)
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)

        return DateFormat(myday, month + 1, year)
    }

    private fun checkThisWeeksHabits(data: List<SelectedHabits>?): MutableList<SelectedHabits> {
        val habitsListSize = data?.size
        val mondayDate = findMondayDate(todayDate())
        val list: MutableList<SelectedHabits> = ArrayList()
        if (data != null) {
            for (habit in data) {
                if (habit.startDate.date >= mondayDate.date || habit.startDate.month >= mondayDate.month)
                    list.add(habit)
            }
        }
        return list
    }


    private fun minusDaysToMonday(dayName: String): Int {
        return when (dayName) {
            "Monday" -> {
                -0
            }
            "Tuesday" -> {
                -1
            }
            "Wednesday" -> {
                -2
            }
            "Thursday" -> {
                -3
            }
            "Friday" -> {
                -4
            }
            "Saturday" -> {
                -5
            }
            "Sunday" -> {
                -6
            }
            else -> {
                -0
            }
        }
    }

    private fun setupRecycler() {
        binding.todayHabitRecycler.adapter = mAdapter
        binding.todayHabitRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupChart() {
        val animationDuration = 1000L
        val barSet = listOf(
            "MON" to 4F,
            "TUE" to 7F,
            "WED" to 2F,
            "FRI" to 2F,
            "SAT" to 5F,
            "SUN" to 4F
        )

        binding.weekChart.labelsColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.weekChart.labelsSize = 12F
        binding.weekChart.animation.duration = animationDuration
        binding.weekChart.animate(barSet)
        binding.weekChart.axis = AxisType.X
    }

    private fun todayDate(): Pair<DateFormat, String> {
        val today = Calendar.getInstance()
        val date = today.time
        val day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.time)

        /**individual item */
        val myday = today.get(Calendar.DATE)
        val month = today.get(Calendar.MONTH)
        val year = today.get(Calendar.YEAR)

        val myDate = DateFormat(myday, month + 1, year)
        return Pair(myDate, day)
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun placeHolderVisibility(visibility: Int) {
        binding.imgNohabits.visibility = visibility
        binding.txtNohabits.visibility = visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
