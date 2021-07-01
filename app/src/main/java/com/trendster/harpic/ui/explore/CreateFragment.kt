package com.trendster.harpic.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.trendster.harpic.databinding.FragmentCreateBinding
import com.trendster.harpic.util.LoadingDialog
import com.trendster.harpic.util.NetworkResult
import com.trendster.harpic.viewmodels.HabitViewModel

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var etHabitName: TextInputLayout
    private lateinit var etGoal: TextInputLayout
    private lateinit var etReward: TextInputLayout
    private lateinit var etNote: TextInputLayout
    private val habitViewModel: HabitViewModel by activityViewModels()
    private lateinit var loadingDialog: LoadingDialog
    var habitDuration = 21
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        loadingDialog = LoadingDialog(requireActivity())

        setupBindings()

        binding.btnDone.setOnClickListener {
            createHabit()
        }

        binding.chipGroupDuration.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedChip = chip.text.toString()
            val duration = checkDays(selectedChip)
            habitDuration = duration
        }

        habitViewModel.savingResponse.observe(
            viewLifecycleOwner,
            { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        showToast("Loading")
                        loadingDialog.startLoading()
                    }
                    is NetworkResult.Error -> {
                        showToast(response.toString())
                        loadingDialog.dismissDialog()
                    }
                    is NetworkResult.Success -> {
                        showToast("Habit Saved Successfully")
                        loadingDialog.dismissDialog()
                        findNavController().navigateUp()
                    }
                }
            }
        )

        return binding.root
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkDays(selectedChip: String): Int {
        return when (selectedChip) {
            "Week" -> {
                7
            }
            "21 days" -> {
                21
            }
            "Month" -> {
                30
            }
            else -> {
                1
            }
        }
    }

    private fun createHabit() {
        when {
            etHabitName.editText?.text?.isEmpty() == true -> {
                etHabitName.error = "Fill this"
            }
            etGoal.editText?.text?.isEmpty() == true -> {
                etGoal.error = "Fill this"
            }
            etReward.editText?.text?.isEmpty() == true -> {
                etReward.error = "Fill this"
            }
            etNote.editText?.text?.isEmpty() == true -> {
                etNote.error = "Fill this"
            }
            else -> {
                habitViewModel.createUserHabit(
                    auth.currentUser?.uid!!,
                    etHabitName.editText?.text.toString(),
                    etGoal.editText?.text.toString(),
                    etReward.editText?.text.toString(),
                    etNote.editText?.text.toString(),
                    habitDuration
                )
            }
        }
    }

    private fun setupBindings() {
        etHabitName = binding.etHabitName
        etGoal = binding.etGoal
        etReward = binding.etReward
        etNote = binding.etNote
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
