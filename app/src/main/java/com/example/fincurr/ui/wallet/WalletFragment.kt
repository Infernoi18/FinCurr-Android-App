package com.example.fincurr.ui.wallet

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
import com.example.fincurr.databinding.FragmentWalletBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.utils.Formatter
import com.example.fincurr.utils.PrefsManager
import com.example.fincurr.viewmodel.WalletViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class WalletFragment : Fragment() {
    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WalletViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }
    private lateinit var prefs: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())

        binding.btnAddBalance.setOnClickListener {
            val amount = binding.editAmount.text?.toString()?.toDoubleOrNull() ?: 0.0
            val note = binding.editNote.text?.toString().orEmpty()
            viewModel.addBalance(amount, note)
        }
        binding.btnSend.setOnClickListener {
            val amount = binding.editAmount.text?.toString()?.toDoubleOrNull() ?: 0.0
            val note = binding.editNote.text?.toString().orEmpty()
            viewModel.sendMoney(amount, note)
        }
        binding.btnReceive.setOnClickListener {
            val amount = binding.editAmount.text?.toString()?.toDoubleOrNull() ?: 0.0
            val note = binding.editNote.text?.toString().orEmpty()
            viewModel.receiveMoney(amount, note)
        }
        binding.btnAddExpense.setOnClickListener {
            val amount = binding.editAmount.text?.toString()?.toDoubleOrNull() ?: 0.0
            val note = binding.editNote.text?.toString().orEmpty()
            val category = binding.editCategory.text?.toString()
            viewModel.addExpense(amount, note, category)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.textWalletBalance.text = Formatter.formatCurrency(state.balance, prefs.currency)
                    state.message?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show() }
                    state.error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show() }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
