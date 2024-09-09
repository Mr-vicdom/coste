package com.expensetracker.app.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.app.category.support.Literals.IS_EXPENSE_LABEL
import com.expensetracker.app.category.viewmodel.CategoryViewModel
import com.expensetracker.app.databinding.CategoryAddScreenBinding
import com.expensetracker.app.transactions.support.SURETY
import com.expensetracker.app.transactions.support.getChoiceAlertDialog
import com.expensetracker.core.support.CategoryType

class CategoryAddActivity: AppCompatActivity() {

    private lateinit var binding: CategoryAddScreenBinding
    private val viewModel: CategoryViewModel by viewModels<CategoryViewModel>()
    private var categoryType: CategoryType = CategoryType.INCOME_CATEGORY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { intent: Intent ->
            intent.getBooleanExtra(IS_EXPENSE_LABEL,false).let {
                if(it) categoryType = CategoryType.EXPENSE_CATEGORY
            }
        }

        binding = CategoryAddScreenBinding.inflate(layoutInflater)

        //Activity Title
        binding.categoryAddTitle.text = categoryType.name.lowercase().replaceFirstChar { it.uppercase() }.replace('_',' ')


        binding.categoryAddBackBtn.setOnClickListener {
            if(binding.categoryNameField.text.toString().isNotEmpty()){
                getChoiceAlertDialog(this,"Discard & Exit",SURETY, onYesClick = {
                    setResult(RESULT_CANCELED)
                    finish()
                }).show()
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }
        }

        binding.categorySaveBtn.setOnClickListener {
            if(binding.categoryNameField.text.toString().isEmpty()){
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addCategory(binding.categoryNameField.text.toString(),categoryType)
            setResult(RESULT_OK)
            finish()
        }

        setContentView(binding.root)
    }
}