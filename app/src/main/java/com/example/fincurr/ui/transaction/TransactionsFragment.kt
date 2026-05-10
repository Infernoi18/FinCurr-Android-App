package com.example.fincurr.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fincurr.FincurrApp
import com.example.fincurr.R
import com.example.fincurr.adapters.TransactionAdapter
import com.example.fincurr.data.model.TransactionType
import com.example.fincurr.databinding.FragmentTransactionsBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.utils.DateUtils
import com.example.fincurr.utils.PrefsManager
import com.example.fincurr.viewmodel.TransactionFilter
import com.example.fincurr.viewmodel.TransactionsViewModel
import kotlinx.coroutines.launch

class TransactionsFragment : Fragment() {
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionsViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }
    private lateinit var adapter: TransactionAdapter
    private lateinit var prefs: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())
        adapter = TransactionAdapter(prefs.currency) { transaction ->
            val args = Bundle().apply { putLong("transactionId", transaction.id) }
            findNavController().navigate(R.id.action_transactionsFragment_to_transactionDetailFragment, args)
        }
        binding.recyclerTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTransactions.adapter = adapter

        binding.editSearch.doAfterTextChanged {
            viewModel.updateQuery(it?.toString().orEmpty())
        }

        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipCredit -> TransactionFilter(type = TransactionType.CREDIT)
                R.id.chipDebit -> TransactionFilter(type = TransactionType.DEBIT)
                R.id.chipFood -> TransactionFilter(category = "Food")
                R.id.chipTravel -> TransactionFilter(category = "Travel")
                R.id.chipBills -> TransactionFilter(category = "Bills")
                R.id.chipMonth -> TransactionFilter(dateRange = DateUtils.monthRange(DateUtils.currentMonthYear()))
                R.id.chipWeek -> {
                    val end = System.currentTimeMillis()
                    val start = end - 7L * 24 * 60 * 60 * 1000
                    TransactionFilter(dateRange = start to end)
                }
                else -> TransactionFilter()
            }
            viewModel.setFilter(filter)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    adapter.updateCurrency(prefs.currency)
                    adapter.submitList(state.transactions)
                    binding.textEmpty.visibility = if (state.transactions.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
