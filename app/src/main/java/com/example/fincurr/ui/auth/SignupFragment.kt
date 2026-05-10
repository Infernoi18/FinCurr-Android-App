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
import androidx.navigation.fragment.findNavController
import com.example.fincurr.FincurrApp
import com.example.fincurr.R
import com.example.fincurr.databinding.FragmentSignupBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignup.setOnClickListener {
            val name = binding.editName.text?.toString().orEmpty()
            val email = binding.editEmail.text?.toString().orEmpty()
            val password = binding.editPassword.text?.toString().orEmpty()
            val confirm = binding.editConfirmPassword.text?.toString().orEmpty()
            when {
                name.isBlank() || email.isBlank() || password.isBlank() -> {
                    Snackbar.make(binding.root, "Fill all fields", Snackbar.LENGTH_SHORT).show()
                }
                password != confirm -> {
                    Snackbar.make(binding.root, "Passwords do not match", Snackbar.LENGTH_SHORT).show()
                }
                else -> viewModel.signup(name, email, password)
            }
        }

        binding.textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.signedUp) {
                        findNavController().navigate(R.id.action_signupFragment_to_pinSetupFragment)
                    }
                    if (state.error != null) {
                        Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
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
