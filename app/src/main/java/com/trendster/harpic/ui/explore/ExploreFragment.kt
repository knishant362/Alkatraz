package com.trendster.harpic.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trendster.harpic.R
import com.trendster.harpic.adapters.ExploreAdapter
import com.trendster.harpic.databinding.FragmentExploreBinding
import com.trendster.harpic.util.NetworkResult
import com.trendster.harpic.viewmodels.MainViewModel

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val mAdapter by lazy { ExploreAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        binding.exploreRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.exploreRecycler.adapter = mAdapter

        mainViewModel.getPredefinedHabits()
        mainViewModel.predefinedHabitsResponse.observe(
            viewLifecycleOwner,
            { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        response.data?.let { mAdapter.setData(it) }
                        hideShimmer()
                        Toast.makeText(requireContext(), response.data?.size.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Error -> {
                        hideShimmer()
                        Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Loading -> {
                        showShimmer()
                    }
                }
            }
        )

        binding.btnCreateHabit.setOnClickListener {
            findNavController().navigate(R.id.action_exploreFragment_to_createFragment)
        }

        return binding.root
    }

    private fun showShimmer() {
        binding.exploreRecycler.showShimmer()
    }

    private fun hideShimmer() {
        binding.exploreRecycler.hideShimmer()
    }

}
