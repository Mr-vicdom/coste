package com.expensetracker.app.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.category.adapter.CategoryListAdapter
import com.expensetracker.app.category.support.Literals.IS_EXPENSE_LABEL
import com.expensetracker.app.category.viewmodel.CategoryViewModel
import com.expensetracker.app.databinding.CategoryScreenBinding
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.ExpenseCategory
import com.expensetracker.core.models.IncomeCategory
import com.expensetracker.core.support.CategoryType
import com.expensetracker.core.support.SimpleName
import kotlin.reflect.typeOf

val TAG = "Cata Ac =>log"

class CategoryActivity : AppCompatActivity(){

    private lateinit var binding: CategoryScreenBinding
    private val viewModel: CategoryViewModel by viewModels<CategoryViewModel>()

    private val categories: MutableList<Category> = mutableListOf()
    private var categoryType: CategoryType = CategoryType.INCOME_CATEGORY
    private val incomeCategoryList: MutableList<IncomeCategory> = categories.mapNotNull { if (it is IncomeCategory) it else null }.toMutableList()
    private val expenseCategoryList: MutableList<ExpenseCategory> = categories.mapNotNull { if (it is ExpenseCategory) it else null }.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { intent: Intent ->
            intent.getBooleanExtra(IS_EXPENSE_LABEL,false).let {
                if(it) categoryType = CategoryType.EXPENSE_CATEGORY
            }
        }

        binding = CategoryScreenBinding.inflate(layoutInflater)

        val onItemChanged = fun(text: String, category: Category) {
            viewModel.updateCategory(text,category)
        }

        val onItemRemoved = fun(category: Category) {
            viewModel.deleteCategory(category)
        }

        val incomeCategoryListAdapter = CategoryListAdapter<IncomeCategory>(incomeCategoryList, onItemChanged = onItemChanged, onItemRemoved = onItemRemoved)
        val expenseCategoryListAdapter = CategoryListAdapter<ExpenseCategory>(expenseCategoryList, onItemChanged = onItemChanged, onItemRemoved = onItemRemoved)

        when(categoryType){
            CategoryType.INCOME_CATEGORY -> binding.categoryScreenListView.adapter =  incomeCategoryListAdapter
            CategoryType.EXPENSE_CATEGORY -> binding.categoryScreenListView.adapter =  expenseCategoryListAdapter
        }

        binding.categoryScreenListView.layoutManager = LinearLayoutManager(this)

        viewModel.fetchCategories(categoryType)

        binding.categoryScreenBackBtn.setOnClickListener {
            finish()
        }

        viewModel.categoryList.observe(this, Observer { categoryList ->
            categories.clear()
            categories.addAll(categoryList)
            when(categoryType){
                CategoryType.INCOME_CATEGORY -> {
                    incomeCategoryList.clear()
                    incomeCategoryList.addAll(categories.mapNotNull { if (it is IncomeCategory) it else null }.toMutableList())
                    binding.categoryScreenListView.adapter = incomeCategoryListAdapter
                    incomeCategoryListAdapter.notifyDataSetChanged()
                }
                CategoryType.EXPENSE_CATEGORY -> {
                    expenseCategoryList.clear()
                    expenseCategoryList.addAll(categories.mapNotNull { if (it is ExpenseCategory) it else null }.toMutableList())
                    binding.categoryScreenListView.adapter = expenseCategoryListAdapter
                    expenseCategoryListAdapter.notifyDataSetChanged()
                }
            }
            Log.d(TAG, "onCreate: $incomeCategoryList")
        })

        setContentView(binding.root)
    }

    override fun onPause() {
        super.onPause()

        viewModel.saveCategories()
    }

}