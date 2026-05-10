package com.example.fincurr.ui.profile

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
import com.example.fincurr.databinding.FragmentProfileBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.utils.PrefsManager
import com.example.fincurr.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }
    private lateinit var prefs: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())
        viewModel.load()

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.editProfileName.text?.toString().orEmpty()
            val email = binding.editProfileEmail.text?.toString().orEmpty()
            viewModel.updateProfile(name, email)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        binding.btnLogout.setOnClickListener {
            prefs.clearSession()
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    state.user?.let { user ->
                        if (binding.editProfileName.text.isNullOrBlank()) {
                            binding.editProfileName.setText(user.fullName)
                        }
                        if (binding.editProfileEmail.text.isNullOrBlank()) {
                            binding.editProfileEmail.setText(user.email)
                        }
                    }
                    state.message?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show() }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
