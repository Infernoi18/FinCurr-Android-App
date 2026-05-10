package com.example.fincurr.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fincurr.FincurrApp
import com.example.fincurr.R
import com.example.fincurr.data.model.TransactionType
import com.example.fincurr.databinding.FragmentTransactionDetailBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.utils.Formatter
import com.example.fincurr.utils.PrefsManager
import com.example.fincurr.viewmodel.TransactionDetailViewModel
import kotlinx.coroutines.launch

class TransactionDetailFragment : Fragment() {
    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionDetailViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }
    private lateinit var prefs: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())
        val transactionId = arguments?.getLong("transactionId") ?: 0L
        viewModel.load(transactionId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    val tx = state.transaction ?: return@collect
                    val amountText = Formatter.formatCurrency(tx.amount, prefs.currency)
                    binding.textDetailAmount.text =
                        if (tx.type == TransactionType.DEBIT) "-$amountText" else amountText
                    val color = if (tx.type == TransactionType.DEBIT) {
                        ContextCompat.getColor(requireContext(), R.color.colorError)
                    } else {
                        ContextCompat.getColor(requireContext(), R.color.colorSuccess)
                    }
                    binding.textDetailAmount.setTextColor(color)
                    binding.textDetailNote.text = tx.note
                    binding.textDetailCategory.text = "Category: ${tx.category}"
                    binding.textDetailType.text = "Type: ${tx.type.name}"
                    binding.textDetailDate.text = "Date: ${Formatter.formatDate(tx.timestamp)}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
