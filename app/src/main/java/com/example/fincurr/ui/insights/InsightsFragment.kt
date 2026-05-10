package com.example.fincurr.ui.insights

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fincurr.FincurrApp
import com.example.fincurr.databinding.FragmentInsightsBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.utils.Formatter
import com.example.fincurr.utils.PrefsManager
import com.example.fincurr.viewmodel.InsightsViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch

class InsightsFragment : Fragment() {
    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InsightsViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }
    private lateinit var prefs: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())
        viewModel.refresh()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.textMonthlySpend.text = Formatter.formatCurrency(state.monthSpend, prefs.currency)
                    binding.textMonthlyIncome.text = Formatter.formatCurrency(state.monthIncome, prefs.currency)
                    updatePieChart(state.categoryTotals)
                    updateBarChart(state.weeklyTotals)
                }
            }
        }
    }

    private fun updatePieChart(categoryTotals: Map<String, Double>) {
        val entries = categoryTotals.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#1F6FEB"),
            Color.parseColor("#6C5CE7"),
            Color.parseColor("#00B894"),
            Color.parseColor("#F59E0B"),
            Color.parseColor("#EF4444")
        )
        dataSet.valueTextColor = Color.WHITE
        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.invalidate()
    }

    private fun updateBarChart(weeklyTotals: List<Double>) {
        val entries = weeklyTotals.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }
        val dataSet = BarDataSet(entries, "")
        dataSet.color = Color.parseColor("#1F6FEB")
        val data = BarData(dataSet)
        data.barWidth = 0.6f
        binding.barChart.data = data
        binding.barChart.description.isEnabled = false
        binding.barChart.legend.isEnabled = false
        binding.barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
