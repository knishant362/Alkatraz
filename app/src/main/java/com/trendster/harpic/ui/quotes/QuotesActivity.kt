package com.trendster.harpic.ui.quotes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.trendster.harpic.adapters.QuotesAdapter
import com.trendster.harpic.databinding.ActivityQuotesBinding
import com.trendster.harpic.util.NetworkResult
import com.trendster.harpic.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuotesActivity : AppCompatActivity() {

    private var _binding: ActivityQuotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mAdapter: QuotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityQuotesBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupRecycler()
        supportActionBar?.hide()

        mainViewModel.fetchQuotes()
        mainViewModel.quotesResponse.observe(
            this,
            { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        showShimmer()
                    }
                    is NetworkResult.Success -> {
                        response.data?.let { mAdapter.setData(it) }
                        hideShimmer()
                    }
                    is NetworkResult.Error -> {
                        showToast(response.message.toString())
                        hideShimmer()
                    }
                }
            }
        )

        binding.closeButton.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setupRecycler() {
        mAdapter = QuotesAdapter()
        binding.quotesRecycler.layoutManager = LinearLayoutManager(this)
        binding.quotesRecycler.adapter = mAdapter
    }

    private fun showShimmer() {
        binding.quotesRecycler.showShimmer()
    }

    private fun hideShimmer() {
        binding.quotesRecycler.hideShimmer()
    }
}
