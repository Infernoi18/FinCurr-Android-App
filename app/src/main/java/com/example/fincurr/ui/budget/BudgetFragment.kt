package com.example.fincurr.ui.budget

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
import com.example.fincurr.databinding.FragmentBudgetBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.utils.Formatter
import com.example.fincurr.utils.PrefsManager
import com.example.fincurr.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BudgetViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }
    private lateinit var prefs: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())
        binding.btnSaveBudget.setOnClickListener {
            val amount = binding.editBudget.text?.toString()?.toDoubleOrNull() ?: 0.0
            viewModel.setBudget(amount)
            viewModel.refreshSpent()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    val budget = state.budget
                    val spent = state.spent
                    val remaining = (budget - spent).coerceAtLeast(0.0)
                    val progress = if (budget <= 0) 0 else ((spent / budget) * 100).toInt().coerceIn(0, 100)
                    binding.budgetProgress.progress = progress
                    binding.textBudgetDetails.text =
                        "Spent ${Formatter.formatCurrency(spent, prefs.currency)} of ${Formatter.formatCurrency(budget, prefs.currency)}. Remaining ${Formatter.formatCurrency(remaining, prefs.currency)}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
