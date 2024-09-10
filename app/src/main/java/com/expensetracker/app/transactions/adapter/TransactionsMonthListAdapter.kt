package com.expensetracker.app.transactions.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.R
import com.expensetracker.app.databinding.MonthTransactionItemBinding
import com.expensetracker.app.databinding.MonthTransactionPeriodicInfoBinding
import com.expensetracker.app.transactions.support.TransactionItemsByMonth
import com.expensetracker.app.transactions.support.WeekNumber
import com.expensetracker.app.views.CurrencyTextView
import java.time.LocalDate
import java.time.Month

class TransactionsMonthListAdapter(
    private val transactionItems: List<TransactionItemsByMonth>,
    private val onItemClickListener: (WeekNumber, Month) -> Unit = { a, b -> },
    private val onLoaded: (TransactionsMonthListAdapter) -> Unit = {}
) : RecyclerView.Adapter<TransactionsMonthListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TransactionsMonthListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TRANSACTION_VIEW_TYPE -> {
                val binding = MonthTransactionItemBinding.inflate(layoutInflater, parent, false)
                TransactionViewHolder(binding)
            }

            PERIODIC_VIEW_TYPE -> {
                val binding =
                    MonthTransactionPeriodicInfoBinding.inflate(layoutInflater, parent, false)
                PeriodicDataViewHolder(binding)
            }

            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE)
        }
    }

    override fun onBindViewHolder(holder: TransactionsMonthListAdapter.ViewHolder, position: Int) {
        if (position == 0)
            Log.d(TAG, "onBindViewHolder: Start")
        if (position == (transactionItems.size-1)) {
            Log.d(TAG, "onBindViewHolder: End")
            onLoaded(this)
        }

        val item = transactionItems[position]
        when (holder) {
            is PeriodicDataViewHolder -> {
                if (item is TransactionItemsByMonth.PeriodicItem)
                    holder.bind(item)
            }

            is TransactionViewHolder -> {
                if (item is TransactionItemsByMonth.TransactionItem) {
                    holder.bind(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (transactionItems[position]) {
            is TransactionItemsByMonth.PeriodicItem -> PERIODIC_VIEW_TYPE
            is TransactionItemsByMonth.TransactionItem -> TRANSACTION_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int = transactionItems.size


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class TransactionViewHolder(
        private val binding: MonthTransactionItemBinding,
    ) : TransactionsMonthListAdapter.ViewHolder(binding.root) {
        private val periodicInfo1: TextView = binding.weekTransPeriodInfoStartDate
        private val periodicInfo2: TextView = binding.weekTransPeriodInfoEndDate
        private val periodicInfoWeek: TextView = binding.weekTransNumber
        private val periodicIncome: CurrencyTextView = binding.weekTransIncome
        private val periodicExpense: CurrencyTextView = binding.weekTransExpense
        private val periodicTotal: CurrencyTextView = binding.weekTransTotal

        fun bind(item: TransactionItemsByMonth.TransactionItem) {
            val data = item.transactionOnWeek

            periodicInfo1.text = data.weekStart
            periodicInfo2.text = data.weekEnd

            val week = data.weekNumber.toString()
            periodicInfoWeek.text = week
            periodicIncome.text = data.totalIncome
            periodicExpense.text = data.totalExpense
            periodicTotal.text = data.total

            binding.root.setOnClickListener {
                onItemClickListener(item.transactionOnWeek.weekNumber,item.transactionOnWeek.startOfWeek.month)
            }
        }
    }

    inner class PeriodicDataViewHolder(
        private val binding: MonthTransactionPeriodicInfoBinding,
    ) : TransactionsMonthListAdapter.ViewHolder(binding.root) {
        private val periodicMonth: TextView = binding.monthTransPeriodInfoMonth
        private val periodicStartDate: TextView = binding.monthTransPeriodInfoStartDate
        private val periodicEndDate: TextView = binding.monthTransPeriodInfoEndDate
        private val periodicIncome: CurrencyTextView = binding.monthTransPeriodicIncome
        private val periodicExpense: CurrencyTextView = binding.monthTransPeriodicExpense
        private val periodicTotal: CurrencyTextView = binding.monthTransPeriodicTotal

        fun bind(item: TransactionItemsByMonth.PeriodicItem) {
            val periodicData = item.periodicData

            periodicMonth.text = periodicData.month.name
            periodicStartDate.text = periodicData.startMonth
            periodicEndDate.text = periodicData.endMonth

            periodicIncome.text = periodicData.totalIncome
            periodicExpense.text = periodicData.totalExpense
            periodicTotal.text = periodicData.total
        }
    }

}


