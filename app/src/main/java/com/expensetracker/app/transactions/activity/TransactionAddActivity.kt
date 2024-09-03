package com.expensetracker.app.transactions.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.expensetracker.app.R
import com.expensetracker.app.databinding.TransactionAddScreenBinding
import com.expensetracker.app.support.Helper.dateToString
import com.expensetracker.app.transactions.support.Literals.ACCOUNT_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.AMOUNT_LABEL
import com.expensetracker.app.transactions.support.Literals.CATEGORY_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.DATE_LABEL
import com.expensetracker.app.transactions.support.Literals.DESCRIPTION_LABEL
import com.expensetracker.app.transactions.support.Literals.NOTE_LABEL
import com.expensetracker.app.transactions.support.getAlertDialog
import com.expensetracker.app.transactions.viewmodel.TransactionManagerViewModel
import com.expensetracker.core.support.Helper
import com.expensetracker.core.support.Literals.DEFAULT_AMOUNT
import com.expensetracker.core.support.TransactionType
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.time.LocalDate

const val DEFAULT_INDEX = 0
const val DEFAULT_STRING = ""
const val EMPTY_CATEGORY_ACCOUNT = "Empty Category or Account"

open class TransactionAddActivity: AppCompatActivity() {

    protected var date: LocalDate = LocalDate.now()
    protected var amount: String = DEFAULT_AMOUNT
    protected var categoryID: Int = DEFAULT_INDEX
    protected var accountID: Int = DEFAULT_INDEX
    protected var note: String = DEFAULT_STRING
    protected var description: String = DEFAULT_STRING

    protected var transactionType: TransactionType = TransactionType.EXPENSE

    protected val expenseCategoryMap: MutableMap<Int, String> = mutableMapOf()
    protected val incomeCategoryMap: MutableMap<Int, String> = mutableMapOf()

    protected val accountMap: MutableMap<Int, String> = mutableMapOf()
    protected open val transactionManagerViewModel: TransactionManagerViewModel by viewModels()
    protected val categoryMap: Map<Int,String>
        get() = when(transactionType){
            TransactionType.INCOME -> incomeCategoryMap
            TransactionType.EXPENSE -> expenseCategoryMap
            TransactionType.TRANSFER -> accountMap
        }

    protected val categoryItems: MutableList<String>
        get() = categoryMap.values.toMutableList()
    protected val accountItems: MutableList<String>
        get() = accountMap.values.toMutableList()

    lateinit var binding: TransactionAddScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState != null) {
            val millis = savedInstanceState.getLong(DATE_LABEL, Helper.dateToMillis(date))
            date = Helper.millisToDate(millis)
            amount = savedInstanceState.getString(AMOUNT_LABEL,amount)
            categoryID = savedInstanceState.getInt(CATEGORY_ID_LABEL,categoryID)
            accountID = savedInstanceState.getInt(ACCOUNT_ID_LABEL,accountID)
            note = savedInstanceState.getString(NOTE_LABEL,note)
            description = savedInstanceState.getString(DESCRIPTION_LABEL,description)
        }

        binding = TransactionAddScreenBinding.inflate(layoutInflater)

        binding.transAddTabBar.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position: Int = tab?.let { if(it.position < 3) it.position else 2 } ?: 2
                transactionManagerViewModel.setTransactionType(TransactionType.entries[position])
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val dateField: TextView = binding.dateField
        val amountField: EditText = binding.amountField
        val categoryField: Spinner = binding.categoryField
        val accountField: Spinner = binding.accountField
        val noteField: EditText = binding.noteField
        val descriptionField: EditText = binding.descriptionField
        val saveBtn: Button = binding.saveBtn

        transactionManagerViewModel.fetchData()

        transactionManagerViewModel.expenseCategories.value?.let {
            expenseCategoryMap.clear()
            expenseCategoryMap.putAll(it)
        }
        transactionManagerViewModel.incomeCategories.value?.let {
            incomeCategoryMap.clear()
            incomeCategoryMap.putAll(it)
        }
        transactionManagerViewModel.accounts.value?.let {
            accountMap.clear()
            accountMap.putAll(it)
        }


        val categoryAdapter = ArrayAdapter<String>(this,R.layout.dropdown_item,categoryItems)
        val accountAdapter = ArrayAdapter<String>(this,R.layout.dropdown_item,accountItems)

        setViewModelObservers(categoryAdapter, accountAdapter, categoryField, accountField)

        //Back Btn
        binding.transAddBackBtn.setOnClickListener {
            onBackClicked()
        }

        //Tab
        if(binding.transAddTabBar.tabCount == 3){
            val tab1 = binding.transAddTabBar.getTabAt(0)
            val tab2 = binding.transAddTabBar.getTabAt(1)
            val tab3 = binding.transAddTabBar.getTabAt(2)
            when(transactionType){
                TransactionType.INCOME -> tab1
                TransactionType.EXPENSE -> tab2
                TransactionType.TRANSFER -> tab3
            }.let { binding.transAddTabBar.selectTab(it) }
        }

        //Date Field
        dateField.text = dateToString(date.dayOfMonth,date.monthValue,date.year)
        val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val text = dateToString(dayOfMonth,month+1,year)
            dateField.text = text
            date = LocalDate.of(year, month+1, dayOfMonth)
        }
        val datePickerDialog = DatePickerDialog(this, androidx.appcompat.R.style.AlertDialog_AppCompat, onDateSetListener, date.year, date.monthValue-1, date.dayOfMonth)
        dateField.setOnClickListener { datePickerDialog.show() }

        //Amount
        amountField.setText(amount)

        amountField.filters = arrayOf(object : InputFilter{
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int,
            ): CharSequence? {
                if(source.isNullOrEmpty()) return ""
                try {
                    val input = dest.toString() + source.toString()
                    if (input.contains('.')) {
                        val decimalPart = input.substringAfter('.')
                        if (decimalPart.length > 2) {
                            return ""
                        }
                    }
                    val inputDouble = input.toDouble()
                    if (inputDouble in 0.0..1000000.00) {
                        return null
                    }
                } catch (_: Exception){ }
                return ""
            }
        })

        //Category & Account Field
        val categoryItemSelectedListener = SpinnerItemSelectedListener{ position ->
            Log.d(TAG, "onCreate: $categoryMap $position")
            categoryID = categoryMap.keys.toList()[position]
        }
        val accountItemSelectedListener = SpinnerItemSelectedListener{ position ->
            accountID = accountMap.keys.toList()[position]
        }

        transactionManagerViewModel.transactionType.observe(this, Observer { type ->
            if(type == TransactionType.TRANSFER){
                binding.categoryLabel.setText(R.string.from_account_label)
                binding.accountLabel.setText(R.string.to_account_label)
            } else {
                binding.categoryLabel.setText(R.string.category_label)
                binding.accountLabel.setText(R.string.account_label)
            }
        })

        categoryField.onItemSelectedListener = categoryItemSelectedListener
        accountField.onItemSelectedListener = accountItemSelectedListener

        categoryField.adapter = categoryAdapter
        accountField.adapter = accountAdapter

        //Note
        noteField.setText(note)
        noteField.addTextChangedListener {
            note = it.toString()
        }

        //Description
        descriptionField.setText(description)
        descriptionField.addTextChangedListener {
            description = it.toString()
        }

        //Save btn
        saveBtn.setOnClickListener {
            amount = amountField.text.toString()
            note = noteField.text.toString()
            description = descriptionField.text.toString()

            when(transactionType){
                TransactionType.INCOME -> transactionManagerViewModel.addIncome(date,amount,note,description,categoryID, accountID)
                TransactionType.EXPENSE -> transactionManagerViewModel.addExpense(date,amount,note,description,categoryID, accountID)
                TransactionType.TRANSFER -> transactionManagerViewModel.addTransfer(date,amount,note,description, fromAccountID = categoryID, toAccountID =  accountID)
            }

            setResult(RESULT_OK)
            finish()
        }

        setContentView(binding.root)
    }

    open fun onBackClicked(): Boolean {
        if(isFieldsEmpty()){
            setResult(RESULT_CANCELED)
            finish()
            return true
        } else {

            val onYesClickListener = {
                setResult(RESULT_CANCELED)
                finish()
            }
            getAlertDialog(this,"Exit","Are You Sure?", onYesClick = onYesClickListener).show()
            return false
        }
    }

    override fun onBackPressed() {
       if(onBackClicked()){
           super.onBackPressed()
       }
    }

    private fun isFieldsEmpty(): Boolean {
        return (note.isEmpty() && description.isEmpty() &&
        date == LocalDate.now() &&
        amount == DEFAULT_AMOUNT)
    }

    private fun setViewModelObservers(categoryAdapter: ArrayAdapter<String>, accountAdapter: ArrayAdapter<String>, categorySpinner: Spinner, accountSpinner: Spinner) {
        transactionManagerViewModel.expenseCategories.observe(this, Observer { data ->
            data.let {
                expenseCategoryMap.clear()
                expenseCategoryMap.putAll(it)
                categoryAdapter.clear()
                categoryAdapter.addAll(categoryItems)
                categoryAdapter.notifyDataSetChanged()
            }
        })
        transactionManagerViewModel.incomeCategories.observe(this, Observer { data ->
            data.let {
                incomeCategoryMap.clear()
                incomeCategoryMap.putAll(it)
                categoryAdapter.clear()
                categoryAdapter.addAll(categoryItems)
                categoryAdapter.notifyDataSetChanged()
            }
        })
        transactionManagerViewModel.accounts.observe(this, Observer { data ->
            data.let {
                accountMap.clear()
                accountMap.putAll(it)
                accountAdapter.clear()
                accountAdapter.addAll(accountItems)
                accountAdapter.notifyDataSetChanged()
                if(transactionType == TransactionType.TRANSFER){
                    categoryAdapter.clear()
                    categoryAdapter.addAll(accountItems)
                    categoryAdapter.notifyDataSetChanged()
                }
            }
        })
        transactionManagerViewModel.isLoaded.observe(this,Observer { data ->
            if(data){
                if(accountMap.isEmpty() || categoryMap.isEmpty()){
                    onDataSetEmpty()
                } else {
                    val categoryPosition = categoryMap.keys.indexOf(categoryID)
                    val accountPosition = accountMap.keys.indexOf(accountID)
                    if(categoryPosition != -1){
                        categorySpinner.setSelection(categoryPosition)
                    }
                    if(accountPosition != -1){
                        accountSpinner.setSelection(accountPosition)
                    }
                }
            }
        })
        transactionManagerViewModel.transactionType.observe(this, Observer { type ->
            if(transactionType != type){
                transactionType = type
                categoryAdapter.clear()
                categoryAdapter.notifyDataSetChanged()
                categoryAdapter.addAll(categoryItems)
                if(categoryMap.isNotEmpty()){
                    categoryID = categoryMap.keys.first()
                    categorySpinner.setSelection(0)
                }
            }
        })
    }

    private fun onDataSetEmpty(){
        Toast.makeText(this, EMPTY_CATEGORY_ACCOUNT,Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong(DATE_LABEL,Helper.dateToMillis(date))
        outState.putString(AMOUNT_LABEL,amount)
        outState.putInt(CATEGORY_ID_LABEL,categoryID)
        outState.putInt(ACCOUNT_ID_LABEL,accountID)
        outState.putString(NOTE_LABEL,note)
        outState.putString(description,description)
    }
}

class SpinnerItemSelectedListener(private val callback: (Int) -> Unit): OnItemSelectedListener {
    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long,
    ) {
        callback(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}