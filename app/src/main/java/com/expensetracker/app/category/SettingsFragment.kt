package com.expensetracker.app.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.expensetracker.app.MainViewModel
import com.expensetracker.app.category.support.Literals.IS_EXPENSE_LABEL
import com.expensetracker.app.databinding.SettingsScreenBinding

class SettingsFragment: Fragment() {

    private lateinit var binding: SettingsScreenBinding
    private val mainViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}