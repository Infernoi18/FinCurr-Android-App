package com.example.fincurr.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fincurr.FincurrApp
import com.example.fincurr.R
import com.example.fincurr.databinding.FragmentLoginBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUser()

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text?.toString().orEmpty()
            val password = binding.editPassword.text?.toString().orEmpty()
            if (email.isBlank() || password.isBlank()) {
                Snackbar.make(binding.root, "Enter email and password", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.login(email, password)
            }
        }

        binding.textSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.btnBiometric.setOnClickListener {
            val user = viewModel.state.value.user
            if (user?.pinHash.isNullOrBlank()) {
                Snackbar.make(binding.root, "Set a PIN to enable biometric", Snackbar.LENGTH_SHORT).show()
            } else {
                showBiometricPrompt()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.error != null) {
                        Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                    }
                    if (state.user != null && state.pinSet) {
                        findNavController().navigate(R.id.action_loginFragment_to_pinVerifyFragment)
                    } else if (state.user != null && !state.pinSet) {
                        findNavController().navigate(R.id.action_loginFragment_to_pinSetupFragment)
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val canAuth = BiometricManager.from(requireContext())
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            Snackbar.make(binding.root, "Biometric not available", Snackbar.LENGTH_SHORT).show()
            return
        }
        val executor = ContextCompat.getMainExecutor(requireContext())
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Verify to continue")
            .setNegativeButtonText("Use PIN")
            .build()
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    viewModel.completeLogin()
                    val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                    findNavController().navigate(R.id.homeFragment, null, navOptions)
                }
            })
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
