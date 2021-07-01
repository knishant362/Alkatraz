package com.trendster.harpic.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.trendster.harpic.databinding.ActivityHabitDetailBinding
import com.trendster.harpic.model.HabitItem
import com.trendster.harpic.util.NetworkResult
import com.trendster.harpic.viewmodels.HabitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HabitDetailActivity : AppCompatActivity() {

    private var _binding: ActivityHabitDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitViewModel: HabitViewModel
    private var thisHabit: HabitItem = HabitItem(0, "", "", "", "", "", 0, 0)
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHabitDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        habitViewModel = ViewModelProvider(this).get(HabitViewModel::class.java)
        supportActionBar?.hide()

        val habitData = intent.getSerializableExtra("Habit")as HabitItem
        thisHabit = habitData

        Toast.makeText(this, habitData.name, Toast.LENGTH_SHORT).show()
        binding.txtHabitName.text = habitData.name
        binding.txtTimeLine.text = habitData.duration.toString()
        binding.txtGoal.text = habitData.remaining.toString()
        binding.txtReward.text = habitData.reward

        binding.btnStartHabit.setOnClickListener {
            if (auth.currentUser?.uid != null)
                habitViewModel.addHabitToUserHabits(auth.currentUser?.uid!!, thisHabit)
        }

        val today = habitViewModel.todayDay()
        Toast.makeText(this, today.toString(), Toast.LENGTH_LONG).show()

        habitViewModel.addingHabitResponse.observe(
            this,
            { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        showToast("Loading")
                    }
                    is NetworkResult.Success -> {
                        val msg = response.data?.message!!
                        if (msg.toString() == "inserted 0 of 1 records") {
                            showToast("This habit already started")
                        }
                        binding.txtHabitName.text = msg
                        showToast(response.data?.message!!)
                    }
                    is NetworkResult.Error -> {
//                        showToast(response.message!!)
                    }
                }
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
