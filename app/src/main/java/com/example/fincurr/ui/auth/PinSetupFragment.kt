package com.example.fincurr.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fincurr.FincurrApp
import com.example.fincurr.R
import com.example.fincurr.databinding.FragmentPinSetupBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PinSetupFragment : Fragment() {
    private var _binding: FragmentPinSetupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSavePin.setOnClickListener {
            val pin = binding.editPin.text?.toString().orEmpty()
            val confirm = binding.editConfirmPin.text?.toString().orEmpty()
            when {
                pin.length < 4 -> Snackbar.make(binding.root, "PIN must be at least 4 digits", Snackbar.LENGTH_SHORT).show()
                pin != confirm -> Snackbar.make(binding.root, "PINs do not match", Snackbar.LENGTH_SHORT).show()
                else -> viewModel.setPin(pin)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.pinSet) {
                        viewModel.completeLogin()
                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                        findNavController().navigate(R.id.homeFragment, null, navOptions)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
