package com.expensetracker.app.transactions.support

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.expensetracker.app.R
import com.expensetracker.app.databinding.TransactionAddScreenAppBarBinding
import com.expensetracker.app.databinding.TransactionAddScreenBinding
import com.expensetracker.app.support.Helper.dateToString
import com.expensetracker.app.transactions.activity.DEFAULT_INDEX
import com.expensetracker.app.transactions.activity.DEFAULT_STRING
import com.expensetracker.app.transactions.activity.SpinnerItemSelectedListener
import com.expensetracker.app.transactions.activity.TAG
import com.expensetracker.app.transactions.support.Literals.ACCOUNT_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.AMOUNT_LABEL
import com.expensetracker.app.transactions.support.Literals.CATEGORY_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.DATE_LABEL
import com.expensetracker.app.transactions.support.Literals.DESCRIPTION_LABEL
import com.expensetracker.app.transactions.support.Literals.NOTE_LABEL
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.viewmodel.TransactionManagerViewModel
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.FinancialTransaction
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.Helper
import com.expensetracker.core.support.Literals.DEFAULT_AMOUNT
import com.expensetracker.core.support.TransactionType
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.time.LocalDate


class TransactionModifyActivityTest : AppCompatActivity() {

    private var id: Int = -1
    private var date: LocalDate = LocalDate.now()
    private var amount: String = DEFAULT_AMOUNT
    private var categoryID: Int = DEFAULT_INDEX
    private var accountID: Int = DEFAULT_INDEX
    private var note: String = DEFAULT_STRING
    private var description: String = DEFAULT_STRING

    private var transaction: Transaction? = null

    private var transactionType: TransactionType = TransactionType.EXPENSE

    private val expenseCategoryMap: MutableMap<Int, String> = mutableMapOf()
    private val incomeCategoryMap: MutableMap<Int, String> = mutableMapOf()

    private val accountMap: MutableMap<Int, String> = mutableMapOf()
    private val transactionManagerViewModel: TransactionManagerViewModel by viewModels()
    private val categoryMap: Map<Int, String>
        get() = when (transactionType) {
            TransactionType.INCOME -> incomeCategoryMap
            TransactionType.EXPENSE -> expenseCategoryMap
            TransactionType.TRANSFER -> accountMap
        }

    private val categoryItems: MutableList<String>
        get() = categoryMap.map { "${it.key} ${it.value}" }.toMutableList()
            // categoryMap.values.toMutableList()
    private val accountItems: MutableList<String>
        get() = accountMap.values.toMutableList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            val millis = savedInstanceState.getLong(DATE_LABEL, Helper.dateToMillis(date))
            date = Helper.millisToDate(millis)
            amount = savedInstanceState.getString(AMOUNT_LABEL, amount)
            categoryID = savedInstanceState.getInt(CATEGORY_ID_LABEL, categoryID)
            accountID = savedInstanceState.getInt(ACCOUNT_ID_LABEL, accountID)
            note = savedInstanceState.getString(NOTE_LABEL, note)
            description = savedInstanceState.getString(DESCRIPTION_LABEL, description)
        } else {
            id = intent.getIntExtra(TRANSACTION_ID_LABEL, id)
            if(id != -1){
                transactionManagerViewModel.fetchTransaction(id)
            }
        }

        val binding = TransactionAddScreenBinding.inflate(layoutInflater)
        val appBarBinding = TransactionAddScreenAppBarBinding.bind(binding.root)

        appBarBinding.transAddBackBtn.setOnClickListener { finish() }
        appBarBinding.transAddTabBar.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position: Int = tab?.let { if (it.position < 3) it.position else 2 } ?: 2
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
        val deleteBtn: Button = binding.deleteBtn
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


        val categoryAdapter = ArrayAdapter<String>(this, R.layout.dropdown_item, categoryItems)
        val accountAdapter = ArrayAdapter<String>(this, R.layout.dropdown_item, accountItems)

        setViewModelObservers(categoryAdapter, accountAdapter, categoryField, accountField)

        //Date Field
        dateField.text = dateToString(date.dayOfMonth, date.monthValue, date.year)
        val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val text = dateToString(dayOfMonth, month, year)
            dateField.text = text
            date = LocalDate.of(year, month, dayOfMonth)
        }
        val datePickerDialog = DatePickerDialog(
            this,
            R.style.TransAddTheme,
            onDateSetListener,
            date.year,
            date.monthValue,
            date.dayOfMonth
        )
        dateField.setOnClickListener { datePickerDialog.show() }

        //Amount
        amountField.setText(amount)

        //Category & Account Field
        val categoryItemSelectedListener = SpinnerItemSelectedListener { position ->
            categoryID = categoryMap.keys.toList()[position]
        }
        val accountItemSelectedListener = SpinnerItemSelectedListener { position ->
            accountID = accountMap.keys.toList()[position]
        }

        transactionManagerViewModel.transactionType.observe(this, Observer { type ->
            if (type == TransactionType.TRANSFER) {
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

        //Description
        descriptionField.setText(description)

        //Save btn
        saveBtn.setOnClickListener {
            amount = amountField.text.toString()
            note = noteField.text.toString()
            description = descriptionField.text.toString()

            Log.d(TAG, "onCreate: $date $amount $categoryID $accountID $note $description")

            if(transaction == null) {
                when (transactionType) {
                    TransactionType.INCOME -> transactionManagerViewModel.addIncome(
                        date,
                        amount,
                        note,
                        description,
                        categoryID,
                        accountID
                    )

                    TransactionType.EXPENSE -> transactionManagerViewModel.addExpense(
                        date,
                        amount,
                        note,
                        description,
                        categoryID,
                        accountID
                    )

                    TransactionType.TRANSFER -> transactionManagerViewModel.addTransfer(
                        date,
                        amount,
                        note,
                        description,
                        fromAccountID = categoryID,
                        toAccountID = accountID
                    )
                }
            }

            setResult(RESULT_OK)
            finish()
        }



        transactionManagerViewModel.existingTransaction.observe(this, Observer { fetchedTransaction: Transaction? ->
            if (fetchedTransaction != null) {
                transaction = fetchedTransaction
                date = Helper.millisToDate(fetchedTransaction.date)
                amount = fetchedTransaction.amount.toString()
                note = fetchedTransaction.note.toString()
                description = fetchedTransaction.description.toString()
                when (fetchedTransaction) {
                    is FinancialTransaction -> {
                        categoryID = fetchedTransaction.category.id
                        accountID = fetchedTransaction.account.id
                    }

                    is Transfer -> {
                        categoryID = fetchedTransaction.fromAccount.id
                        accountID = fetchedTransaction.toAccount.id
                    }
                }
                val categoryPosition = categoryMap.keys.indexOf(categoryID)
                if (categoryPosition != -1) categoryField.setSelection(categoryPosition)
                val accountPosition = categoryMap.keys.indexOf(accountID)
                if (accountPosition != -1) categoryField.setSelection(accountPosition)


                if(appBarBinding.transAddTabBar.tabCount == 3){
                    val tab1 = appBarBinding.transAddTabBar.getTabAt(0)
                    val tab2 = appBarBinding.transAddTabBar.getTabAt(1)
                    val tab3 = appBarBinding.transAddTabBar.getTabAt(2)
                    when(fetchedTransaction){
                        is Expense -> tab2
                        is Income -> tab1
                        is Transfer -> tab3
                    }.let { appBarBinding.transAddTabBar.selectTab(it) }
                }

                dateField.text = date.toString()
                amountField.setText(amount)
                noteField.setText(note)
                descriptionField.setText(description)
                deleteBtn.visibility = View.VISIBLE

                deleteBtn.setOnClickListener {
                    transactionManagerViewModel.deleteTransaction(fetchedTransaction)
                    setResult(RESULT_OK)
                    finish()
                }

                saveBtn.setOnClickListener {
                    amount = amountField.text.toString()
                    note = noteField.text.toString()
                    description = descriptionField.text.toString()

                    when (transactionType) {
                        TransactionType.INCOME -> transactionManagerViewModel.updateIncome(
                            fetchedTransaction,
                            date,
                            amount,
                            note,
                            description,
                            categoryID,
                            accountID
                        )

                        TransactionType.EXPENSE -> transactionManagerViewModel.updateExpense(
                            fetchedTransaction,
                            date,
                            amount,
                            note,
                            description,
                            categoryID,
                            accountID
                        )

                        TransactionType.TRANSFER -> transactionManagerViewModel.updateTransfer(
                            fetchedTransaction,
                            date,
                            amount,
                            note,
                            description,
                            fromAccountID = categoryID,
                            toAccountID = accountID
                        )
                    }
                    setResult(RESULT_OK)
                    finish()
                }

            } else {
                Toast.makeText(this, "No Transaction Found $id", Toast.LENGTH_SHORT).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        })


        setContentView(binding.root)
    }

    private fun setViewModelObservers(
        categoryAdapter: ArrayAdapter<String>,
        accountAdapter: ArrayAdapter<String>,
        categorySpinner: Spinner,
        accountSpinner: Spinner,
    ) {
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
                if (transactionType == TransactionType.TRANSFER) {
                    categoryAdapter.clear()
                    categoryAdapter.addAll(accountItems)
                    categoryAdapter.notifyDataSetChanged()
                }
            }
        })
        transactionManagerViewModel.isLoaded.observe(this, Observer { data ->
            if (data) {
                if (accountMap.isEmpty() || categoryMap.isEmpty()) {
                    onDataSetEmpty()
                } else {
                    val categoryPosition = categoryMap.keys.indexOf(categoryID)
                    val accountPosition = accountMap.keys.indexOf(accountID)
                    if (categoryPosition != -1) {
                        categorySpinner.setSelection(categoryPosition)
                    }
                    if (accountPosition != -1) {
                        accountSpinner.setSelection(accountPosition)
                    }
                }
            }
        })
        transactionManagerViewModel.transactionType.observe(this, Observer { type ->
            transactionType = type
            categoryAdapter.clear()
            categoryAdapter.addAll(categoryItems)
            categoryAdapter.notifyDataSetChanged()
            if(categoryMap.isNotEmpty()) categoryID = categoryMap.keys.first()
        })
    }

    private fun onDataSetEmpty() {
        Toast.makeText(this, "Empty Category or Account", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong(DATE_LABEL, Helper.dateToMillis(date))
        outState.putString(AMOUNT_LABEL, amount)
        outState.putInt(CATEGORY_ID_LABEL, categoryID)
        outState.putInt(ACCOUNT_ID_LABEL, accountID)
        outState.putString(NOTE_LABEL, note)
        outState.putString(description, description)
    }

    override fun onDestroy() {
        super.onDestroy()
        transactionManagerViewModel
    }
}