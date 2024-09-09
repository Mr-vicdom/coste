package com.expensetracker.app.transactions.adapter

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

class TransactionsMonthListAdapter(
    private val transactionItems: List<TransactionItemsByMonth>,
    private val onItemClickListener: (WeekNumber) -> Unit = {},
) : RecyclerView.Adapter<TransactionsMonthListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TransactionsMonthListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TRANSACTION_VIEW_TYPE -> {
                val binding = MonthTransactionItemBinding.inflate(layoutInflater, parent, false)
                TransactionViewHolder(binding, binding.root)
            }

            PERIODIC_VIEW_TYPE -> {
                val binding =
                    MonthTransactionPeriodicInfoBinding.inflate(layoutInflater, parent, false)
                PeriodicDataViewHolder(binding, binding.root)
            }

            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE)
        }
    }

    override fun onBindViewHolder(holder: TransactionsMonthListAdapter.ViewHolder, position: Int) {
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
        itemView: View,
    ) : TransactionsMonthListAdapter.ViewHolder(itemView) {
        private val periodicInfo1: TextView = binding.weekTransPeriodInfoStartDate
        private val periodicInfo2: TextView = binding.weekTransPeriodInfoEndDate
        private val periodicInfoWeek: TextView = binding.weekTransNumber
        private val periodicIncome: CurrencyTextView = binding.weekTransIncome
        private val periodicExpense: CurrencyTextView = binding.weekTransExpense
        private val periodicTotal: CurrencyTextView = binding.weekTransTotal

        private val expenseColor =
            ContextCompat.getColor(itemView.context, R.color.primaryContentColor)
        private val incomeColor =
            ContextCompat.getColor(itemView.context, R.color.secondaryContentColor)
        private val transferColor = ContextCompat.getColor(itemView.context, R.color.textSecondary)

        fun bind(item: TransactionItemsByMonth.TransactionItem) {
            val data = item.transactionOnWeek

            val startWeek = data.startOfWeek
            val endWeek = data.endOfWeek

            var weekStart = startWeek.dayOfMonth.toString()
            var weekEnd = endWeek.dayOfMonth.toString()
            if (startWeek == endWeek) {
                if (startWeek.dayOfMonth == 1) weekStart = ""
                else if (startWeek.dayOfMonth == startWeek.lengthOfMonth()) weekEnd = ""
            }

            var date = weekStart
            if (date.length == 1) date = "0$date"
            periodicInfo1.text = date

            date = weekEnd
            if (date.length == 1) date = "0$date"
            periodicInfo2.text = date

            val week = data.weekNumber.toString()
            periodicInfoWeek.text = week
            periodicIncome.text = data.totalIncome
            periodicExpense.text = data.totalExpense
            var total = 0.0
            try {
                total = data.totalIncome.toDouble() - data.totalExpense.toDouble()
            } catch (_: Exception) {
            }
            periodicTotal.text = total.toString()

            binding.root.setOnClickListener {
                onItemClickListener(item.transactionOnWeek.weekNumber)
            }
        }
    }

    inner class PeriodicDataViewHolder(
        private val binding: MonthTransactionPeriodicInfoBinding,
        itemView: View,
    ) : TransactionsMonthListAdapter.ViewHolder(itemView) {
        private val periodicMonth: TextView = binding.monthTransPeriodInfoMonth
        private val periodicStartDate: TextView = binding.monthTransPeriodInfoStartDate
        private val periodicEndDate: TextView = binding.monthTransPeriodInfoEndDate
        private val periodicIncome: CurrencyTextView = binding.monthTransPeriodicIncome
        private val periodicExpense: CurrencyTextView = binding.monthTransPeriodicExpense
        private val periodicTotal: CurrencyTextView = binding.monthTransPeriodicTotal

        fun bind(item: TransactionItemsByMonth.PeriodicItem) {
            val periodicData = item.periodicData

            periodicMonth.text = periodicData.month.name
            val startMonth = "01"
            val endMonth = periodicData.month.length(periodicData.year.isLeap).toString()

            periodicStartDate.text = startMonth
            periodicEndDate.text = endMonth

            periodicIncome.text = periodicData.totalIncome
            periodicExpense.text = periodicData.totalExpense
            var total = 0.0
            try {
                total = periodicData.totalIncome.toDouble() - periodicData.totalExpense.toDouble()
            } catch (_: Exception) {
            }
            periodicTotal.text = total.toString()
        }
    }

}


