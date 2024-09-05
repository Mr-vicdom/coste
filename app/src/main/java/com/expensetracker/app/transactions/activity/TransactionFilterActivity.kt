package com.expensetracker.app.transactions.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.databinding.TransactionFilterScreenCoBinding
import com.expensetracker.app.support.TAG
import com.expensetracker.app.transactions.adapter.TransactionsFilterAdapter
import com.expensetracker.app.transactions.support.Literals.FILTER_ACCOUNT_IDS_LABEL
import com.expensetracker.app.transactions.support.Literals.MONTH_LABEL
import com.expensetracker.app.transactions.support.Literals.YEAR_LABEL
import com.expensetracker.app.transactions.support.TransactionFilterData
import com.expensetracker.app.transactions.viewmodel.TransactionFilterViewModel
import com.expensetracker.core.models.AccountID
import java.time.LocalDate
import java.time.Month
import java.time.Year


const val TOTAL_INCOME = "TotalIncome"
const val TOTAL_EXPENSE = "TotalExpense"
const val SELECTED_INCOME = "SelectedIncome"
const val SELECTED_EXPENSE = "SelectedExpense"

class TransactionFilterActivity: AppCompatActivity() {

    private lateinit var binding : TransactionFilterScreenCoBinding
    private val transactionFilterViewModel: TransactionFilterViewModel by viewModels<TransactionFilterViewModel>()

    private var month : Month = LocalDate.now().month
    private var year: Year = Year.now()

    private var totalIncome: Double = 0.0
    private var totalExpense: Double  = 0.0
    private val totalBalance: Double
        get() = totalIncome - totalExpense

    private var selectedIncome: Double = 0.0
    private var selectedExpense: Double  = 0.0
    private val selectedBalance: Double
        get() = selectedIncome - selectedExpense

    private val selectedAccountIds: MutableSet<AccountID> = mutableSetOf()
    private val transactionFilterData: MutableList<TransactionFilterData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val monthValue = intent.getIntExtra(MONTH_LABEL, month.value)
        if (monthValue in 1..12) {
            month = Month.of(monthValue)
        }
        try {
            year = Year.of(intent.getIntExtra(YEAR_LABEL, year.value))
        } catch (_: Exception) {
        }
        intent.getIntArrayExtra(FILTER_ACCOUNT_IDS_LABEL)?.let {
            selectedAccountIds.clear()
            selectedAccountIds.addAll(it.toList())
        }

        transactionFilterViewModel.getSelectedIds()

        binding = TransactionFilterScreenCoBinding.inflate(layoutInflater)

        binding.transFilterMonth.text = month.name
        binding.transFilterCloseBtn.setOnClickListener {
            val resultIntent = Intent()
            setResult(RESULT_CANCELED, resultIntent)
            finish()
        }

        //Filter BTN
        binding.transFilterBtn.visibility = View.GONE
        binding.transFilterBtn.setOnClickListener {
            transactionFilterViewModel.getSelectedIds()
            transactionFilterViewModel.selectedAccounts.observe(this, Observer {
                val resultIntent = Intent()
                Log.d(TAG, "onCreate: $it")
                resultIntent.putExtra(FILTER_ACCOUNT_IDS_LABEL, it.toIntArray())
                setResult(RESULT_OK, resultIntent)
                finish()
            })
        }

        val totalIncomeObserver = Observer<Double> {
            totalIncome = it
            binding.transFilterInfo1.text = totalIncome.toString()
            binding.transFilterInfo3.text = totalBalance.toString()
        }

        val totalExpenseObserver = Observer<Double> {
            totalExpense = it
            binding.transFilterInfo2.text = totalExpense.toString()
            binding.transFilterInfo3.text = totalBalance.toString()
        }

        val listView = binding.transFilterList
        val adapter = TransactionsFilterAdapter(
            transactionFilterData,
            selectedAccountIds,
            mutableMapOf(),
            onFilterApplied = { data ->
                transactionFilterViewModel.updateTotals(data)
            },
            onFilterRemoved = { data ->
                transactionFilterViewModel.updateTotals(data)
            })

        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(this)

        transactionFilterViewModel.prepareTransactionFilter(year, month, selectedAccountIds)

        transactionFilterViewModel.selectedAccounts.observe(this, Observer {
            selectedAccountIds.clear()
            selectedAccountIds.addAll(it)
            if (selectedAccountIds.isNotEmpty()) {
                binding.transFilterBtn.visibility = View.VISIBLE
                transactionFilterViewModel.totalIncome.removeObserver(totalIncomeObserver)
                transactionFilterViewModel.totalExpense.removeObserver(totalExpenseObserver)
            } else {
                binding.transFilterBtn.visibility = View.GONE
                transactionFilterViewModel.totalIncome.observe(this, totalIncomeObserver)
                transactionFilterViewModel.totalExpense.observe(this, totalExpenseObserver)
            }
        })

        transactionFilterViewModel.transactionFilterData.observe(this, Observer {
            transactionFilterData.clear()
            transactionFilterData.addAll(it)
            adapter.notifyDataSetChanged()
        })

        transactionFilterViewModel.totalIncome.observe(this, totalIncomeObserver)
        transactionFilterViewModel.totalExpense.observe(this, totalExpenseObserver)

        transactionFilterViewModel.selectedIncome.observe(this, Observer {
            selectedIncome = it
            binding.transFilterInfo1.text = selectedIncome.toString()
            binding.transFilterInfo3.text = selectedBalance.toString()
        })

        transactionFilterViewModel.selectedExpense.observe(this, Observer {
            selectedExpense = it
            binding.transFilterInfo2.text = selectedExpense.toString()
            binding.transFilterInfo3.text = selectedBalance.toString()
        })

        setContentView(binding.root)

    }

}