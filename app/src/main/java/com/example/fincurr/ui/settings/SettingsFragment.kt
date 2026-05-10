package com.example.fincurr.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fincurr.FincurrApp
import com.example.fincurr.R
import com.example.fincurr.databinding.FragmentSettingsBinding
import com.example.fincurr.di.AppViewModelFactory
import com.example.fincurr.utils.PrefsManager
import com.example.fincurr.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as FincurrApp)
    }
    private lateinit var prefs: PrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.currency_options)
        )
        binding.dropCurrency.setAdapter(adapter)

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotifications(isChecked)
        }

        binding.dropCurrency.setOnItemClickListener { _, _, position, _ ->
            val value = adapter.getItem(position) ?: "INR"
            viewModel.setCurrency(value)
        }

        binding.btnLogout.setOnClickListener {
            prefs.clearSession()
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (binding.switchDarkMode.isChecked != state.darkMode) {
                        binding.switchDarkMode.isChecked = state.darkMode
                    }
                    if (binding.switchNotifications.isChecked != state.notificationsEnabled) {
                        binding.switchNotifications.isChecked = state.notificationsEnabled
                    }
                    if (binding.dropCurrency.text.toString() != state.currency) {
                        binding.dropCurrency.setText(state.currency, false)
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
