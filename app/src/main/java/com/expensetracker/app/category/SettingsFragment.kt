package com.expensetracker.app.category

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.expensetracker.app.MainViewModel
import com.expensetracker.app.R
import com.expensetracker.app.category.support.Literals.IS_EXPENSE_LABEL
import com.expensetracker.app.databinding.SettingsScreenBinding
import com.google.android.material.color.MaterialColors

class SettingsFragment: Fragment() {

    private lateinit var binding: SettingsScreenBinding
    private val mainViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        val color = MaterialColors.getColor(requireContext(), androidx.appcompat.R.attr.colorPrimary, colorPrimary)
        requireActivity().window.statusBarColor = color

        binding = SettingsScreenBinding.inflate(layoutInflater)

        binding.incomeCategoriesSettings.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            startActivity(intent)
        }

        binding.expenseCategoriesSettings.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra(IS_EXPENSE_LABEL,true)
            startActivity(intent)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            mainViewModel.postBackPressed()
        }

        return binding.root
    }

}