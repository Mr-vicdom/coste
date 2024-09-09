package com.expensetracker.app.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.category.adapter.CategoryListAdapter
import com.expensetracker.app.category.support.Literals.IS_EXPENSE_LABEL
import com.expensetracker.app.category.viewmodel.CategoryViewModel
import com.expensetracker.app.databinding.CategoryScreenBinding
import com.expensetracker.app.transactions.support.SURETY
import com.expensetracker.app.transactions.support.getChoiceAlertDialog
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.ExpenseCategory
import com.expensetracker.core.models.IncomeCategory
import com.expensetracker.core.support.CategoryType

val TAG = "Cata Ac =>log"

class CategoryActivity : AppCompatActivity(){

    private lateinit var binding: CategoryScreenBinding
    private val viewModel: CategoryViewModel by viewModels<CategoryViewModel>()

    private var categoryType: CategoryType = CategoryType.INCOME_CATEGORY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { intent: Intent ->
            intent.getBooleanExtra(IS_EXPENSE_LABEL,false).let {
                if(it) categoryType = CategoryType.EXPENSE_CATEGORY.also { Log.d(TAG, "onCreate: Its Expense now") }
            }
        }

        binding = CategoryScreenBinding.inflate(layoutInflater)

        //Activity Title
        binding.categoryScreenTitle.text = categoryType.name.lowercase().replaceFirstChar { it.uppercase() }.replace('_',' ')

        val onSaveTrigger = fun(text: String, category: Category, position: Int) {
            if (text.isNotEmpty() && text != category.name.toString()) viewModel.updateCategory(text,category,position)
        }

        val onItemRemoved = fun(category: Category, position: Int) {
            getChoiceAlertDialog(this,"Delete Category",SURETY, onYesClick = {
                viewModel.deleteCategory(category, position)
            }).show()
        }

        val incomeCategoryListAdapter = CategoryListAdapter<IncomeCategory>(viewModel.incomeCategoriesList, onSaveTrigger = onSaveTrigger, onItemRemoved = onItemRemoved)
        val expenseCategoryListAdapter = CategoryListAdapter<ExpenseCategory>(viewModel.expenseCategoriesList, onSaveTrigger = onSaveTrigger, onItemRemoved = onItemRemoved)

        when(categoryType){
            CategoryType.INCOME_CATEGORY -> binding.categoryScreenListView.adapter =  incomeCategoryListAdapter
            CategoryType.EXPENSE_CATEGORY -> binding.categoryScreenListView.adapter =  expenseCategoryListAdapter
        }

        binding.categoryScreenListView.layoutManager = LinearLayoutManager(this)

        viewModel.fetchCategories(categoryType)

        viewModel.updatePosition.observe(this, Observer {
            Log.d(TAG, "onUpdated: position ; $it")
            when(categoryType){
                CategoryType.INCOME_CATEGORY -> incomeCategoryListAdapter.notifyItemChanged(it)
                CategoryType.EXPENSE_CATEGORY -> expenseCategoryListAdapter.notifyItemChanged(it)
            }
        })

        viewModel.deletePosition.observe(this, Observer {
            Log.d(TAG, "onDeleted: position ; $it")
            when(categoryType){
                CategoryType.INCOME_CATEGORY -> incomeCategoryListAdapter.notifyDataSetChanged()
                CategoryType.EXPENSE_CATEGORY -> expenseCategoryListAdapter.notifyDataSetChanged()
            }
        })

        viewModel.addPosition.observe(this, Observer {
            Log.d(TAG, "onAdded: position ; $it")
            when(categoryType){
                CategoryType.INCOME_CATEGORY -> incomeCategoryListAdapter.notifyDataSetChanged()
                CategoryType.EXPENSE_CATEGORY -> expenseCategoryListAdapter.notifyDataSetChanged()
            }
        })

        binding.categoryScreenBackBtn.setOnClickListener {
            finish()
        }

        val addCategoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                when(categoryType){
                    CategoryType.INCOME_CATEGORY -> viewModel.fetchCategories(categoryType)
                    CategoryType.EXPENSE_CATEGORY -> viewModel.fetchCategories(categoryType)
                }
            }
        }

        binding.categoryScreenAddBtn.setOnClickListener {
            val intent = Intent(this, CategoryAddActivity::class.java)
            if (categoryType == CategoryType.EXPENSE_CATEGORY) intent.putExtra(IS_EXPENSE_LABEL,true)
            addCategoryLauncher.launch(intent)
        }

        setContentView(binding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Toast.makeText(this, if(resultCode == RESULT_OK) "RESULT_OK" else "RESULT NO", Toast.LENGTH_SHORT).show()
        super.onActivityResult(requestCode, resultCode, data)
    }

}