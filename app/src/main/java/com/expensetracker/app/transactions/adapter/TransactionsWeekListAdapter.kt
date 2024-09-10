package com.expensetracker.app.transactions.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.R
import com.expensetracker.app.databinding.TransactionItemBinding
import com.expensetracker.app.databinding.TransactionPeriodicInfoBinding
import com.expensetracker.app.databinding.WeekTransactionItemBinding
import com.expensetracker.app.databinding.WeekTransactionPeriodicInfoBinding
import com.expensetracker.app.transactions.support.TransactionItemsByWeek
import com.expensetracker.app.views.CurrencyTextView
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.FinancialTransaction
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import java.time.DayOfWeek
import java.time.LocalDate

class TransactionsWeekListAdapter(
    private val transactionItems: List<TransactionItemsByWeek>,
    private val onItemClickListener: (LocalDate) -> Unit = {},
) : RecyclerView.Adapter<TransactionsWeekListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TransactionsWeekListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TRANSACTION_VIEW_TYPE -> {
                val binding = WeekTransactionItemBinding.inflate(layoutInflater, parent, false)
                TransactionViewHolder(binding)
            }

            PERIODIC_VIEW_TYPE -> {
                val binding =
                    WeekTransactionPeriodicInfoBinding.inflate(layoutInflater, parent, false)
                PeriodicDataViewHolder(binding)
            }

            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE)
        }
    }

    override fun onBindViewHolder(holder: TransactionsWeekListAdapter.ViewHolder, position: Int) {
        val item = transactionItems[position]
        when (holder) {
            is PeriodicDataViewHolder -> {
                if (item is TransactionItemsByWeek.PeriodicItem)
                    holder.bind(item)
            }

            is TransactionViewHolder -> {
                if (item is TransactionItemsByWeek.TransactionItem) {
                    holder.bind(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (transactionItems[position]) {
            is TransactionItemsByWeek.PeriodicItem -> PERIODIC_VIEW_TYPE
            is TransactionItemsByWeek.TransactionItem -> TRANSACTION_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int = transactionItems.size


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class TransactionViewHolder(
        private val binding: WeekTransactionItemBinding,
    ) : TransactionsWeekListAdapter.ViewHolder(binding.root) {
        private val income: CurrencyTextView = binding.periodicIncome
        private val expense: CurrencyTextView = binding.periodicExpense
        private val day: TextView = binding.periodicDay
        private val date: TextView = binding.periodInfo

        private val expenseColor =
            ContextCompat.getColor(itemView.context, R.color.primaryContentColor)
        private val incomeColor =
            ContextCompat.getColor(itemView.context, R.color.secondaryContentColor)
        private val transferColor = ContextCompat.getColor(itemView.context, R.color.textSecondary)

        fun bind(item: TransactionItemsByWeek.TransactionItem) {
            val data = item.transactionOnDay

            income.text = data.totalIncome
            expense.text = data.totalExpense
            var dayOfMonth = data.dayOfMonth
            if (dayOfMonth.length == 1) dayOfMonth = "0$dayOfMonth"
            date.text = dayOfMonth
            day.text = data.dayOfWeek.name

            when (data.dayOfWeek) {
                DayOfWeek.SATURDAY -> incomeColor
                DayOfWeek.SUNDAY -> expenseColor
                else -> transferColor
            }.let { day.setTextColor(it) }

            itemView.setOnClickListener {
                onItemClickListener(data.date)
            }
        }
    }

    inner class PeriodicDataViewHolder(
        private val binding: WeekTransactionPeriodicInfoBinding,
    ) : TransactionsWeekListAdapter.ViewHolder(binding.root) {
        private val periodicInfo1: TextView = binding.weekTransPeriodInfoDate1
        private val periodicInfo2: TextView = binding.weekTransPeriodInfoDate2
        private val periodicInfoWeek: TextView = binding.weekTransNumber
        private val periodicIncome: CurrencyTextView = binding.weekTransPeriodicIncome
        private val periodicExpense: CurrencyTextView = binding.weekTransPeriodicExpense
        private val periodicTotal: CurrencyTextView = binding.weekTransPeriodicTotal

        fun bind(item: TransactionItemsByWeek.PeriodicItem) {
            val periodicData = item.periodicData

            val startWeek = periodicData.startOfWeek
            val endWeek = periodicData.endOfWeek

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

            val week = periodicData.weekNumber.toString()
            periodicInfoWeek.text = week
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


