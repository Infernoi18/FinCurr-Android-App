package com.example.fincurr.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.fincurr.databinding.FragmentHomeBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.utils.Formatter
import com.example.fincurr.utils.PrefsManager
import com.example.fincurr.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }
    private lateinit var prefs: PrefsManager
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())
        adapter = TransactionAdapter(prefs.currency) { transaction ->
            val args = Bundle().apply { putLong("transactionId", transaction.id) }
            findNavController().navigate(R.id.transactionDetailFragment, args)
        }
        binding.recyclerRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecent.adapter = adapter

        binding.btnQuickSend.setOnClickListener {
            findNavController().navigate(R.id.walletFragment)
        }
        binding.btnQuickReceive.setOnClickListener {
            findNavController().navigate(R.id.walletFragment)
        }
        binding.btnQuickAdd.setOnClickListener {
            findNavController().navigate(R.id.walletFragment)
        }
        binding.btnViewBudget.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_budgetFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.textBalance.text = Formatter.formatCurrency(state.balance, prefs.currency)
                    binding.textMonthlySpend.text = Formatter.formatCurrency(state.monthlySpend, prefs.currency)
                    adapter.submitList(state.recentTransactions)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
