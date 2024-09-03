package com.expensetracker.app.category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.app.category.support.Literals.IS_EXPENSE_LABEL
import com.expensetracker.app.databinding.SettingsScreenBinding

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = SettingsScreenBinding.inflate(layoutInflater)

        binding.incomeCategoriesSettings.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        binding.expenseCategoriesSettings.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            intent.putExtra(IS_EXPENSE_LABEL,true)
            startActivity(intent)
        }

        setContentView(binding.root)
    }
}