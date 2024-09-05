package com.expensetracker.app.transactions.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.R
import com.expensetracker.app.databinding.TransactionItemBinding
import com.expensetracker.app.databinding.TransactionPeriodicInfoBinding
import com.expensetracker.app.transactions.support.PeriodicDataByDay
import com.expensetracker.app.transactions.support.PeriodicDataByMonth
import com.expensetracker.app.transactions.support.PeriodicDataByYear
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.views.CurrencyTextView
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.FinancialTransaction
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import java.time.DayOfWeek

const val TRANSFER_CATEGORY = "Transfer"
const val TRANSFER_ACCOUNT_SYMBOL = "->"
const val INVALID_VIEW_TYPE = "Invalid ViewType passed"
const val TAG = "TransactionsListAdapter=>log"

const val TRANSACTION_VIEW_TYPE = 0
const val PERIODIC_VIEW_TYPE = 1

class TransactionsListAdapter(
    private val transactionItems: List<TransactionItems>,
    private val onItemClickListener: (Transaction) -> Unit = {}
): RecyclerView.Adapter<TransactionsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType){
            TRANSACTION_VIEW_TYPE -> {
                val binding = TransactionItemBinding.inflate(layoutInflater,parent,false)
                TransactionViewHolder(binding,binding.root)
            }
            PERIODIC_VIEW_TYPE -> {
                val binding = TransactionPeriodicInfoBinding.inflate(layoutInflater,parent,false)
                PeriodicDataViewHolder(binding,binding.root)
            }
            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE)
        }
    }

    override fun onBindViewHolder(holder: TransactionsListAdapter.ViewHolder, position: Int) {
        val item = transactionItems[position]
        when(holder){
            is PeriodicDataViewHolder -> {
                if(item is TransactionItems.PeriodicItem)
                    holder.bind(item)
            }
            is TransactionViewHolder -> {
                if(item is TransactionItems.TransactionItem) {
                    holder.bind(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(transactionItems[position]){
            is TransactionItems.PeriodicItem -> PERIODIC_VIEW_TYPE
            is TransactionItems.TransactionItem -> TRANSACTION_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int = transactionItems.size


    sealed class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    inner class TransactionViewHolder(private val binding: TransactionItemBinding, itemView: View): TransactionsListAdapter.ViewHolder(itemView) {
        private val transCategory: TextView = binding.transCategory
        private val transNote: TextView = binding.transNote
        private val transAccount: TextView = binding.transAccount
        private val transAmount: CurrencyTextView = binding.transAmount

        private val expenseColor = ContextCompat.getColor(itemView.context, R.color.primaryContentColor)
        private val incomeColor = ContextCompat.getColor(itemView.context, R.color.secondaryContentColor)
        private val transferColor = ContextCompat.getColor(itemView.context, R.color.textSecondary)

        fun bind(item: TransactionItems.TransactionItem) {
            val transaction = item.transaction
            when(transaction){
                is FinancialTransaction -> {
                    transCategory.text = transaction.category.name.toString()
                    transAccount.text = transaction.account.name.toString()
                    when(transaction){
                        is Expense -> transAmount.setTextColor(expenseColor)
                        is Income -> transAmount.setTextColor(incomeColor)
                    }
                }
                is Transfer -> {
                    transCategory.text = TRANSFER_CATEGORY
                    val account = "${transaction.fromAccount.name} $TRANSFER_ACCOUNT_SYMBOL ${transaction.toAccount.name}"
                    transAccount.text = account
                    transAmount.setTextColor(transferColor)
                }
            }

            if (transaction.note.toString().isEmpty()){
                transNote.visibility = View.GONE
            } else {
                val x = "${transaction.note}"
                transNote.text = x
                transNote.visibility = View.VISIBLE
            }
            transAmount.text = transaction.amount.toString()

            itemView.setOnClickListener {
                onItemClickListener(item.transaction)
            }

        }
    }

    inner class PeriodicDataViewHolder(private val binding: TransactionPeriodicInfoBinding, itemView: View): TransactionsListAdapter.ViewHolder(itemView) {
        private val periodicInfo : TextView = binding.periodInfo
        private val periodicDay : TextView = binding.periodicDay
        private val periodicIncome: CurrencyTextView = binding.periodicIncome
        private val periodicExpense: CurrencyTextView = binding.periodicExpense

        private val expenseColor = ContextCompat.getColor(itemView.context, R.color.primaryContentColor)
        private val incomeColor = ContextCompat.getColor(itemView.context, R.color.secondaryContentColor)
        private val transferColor = ContextCompat.getColor(itemView.context, R.color.textSecondary)

        fun bind(item: TransactionItems.PeriodicItem) {
            val periodicData = item.periodicData
            when(periodicData){
                is PeriodicDataByDay -> {
                    periodicDay.visibility = View.VISIBLE
                    periodicInfo.text = periodicData.dayOfMonth
                    periodicDay.text = periodicData.dayOfWeek.name

                    when(periodicData.dayOfWeek){
                        DayOfWeek.SATURDAY -> incomeColor
                        DayOfWeek.SUNDAY -> expenseColor
                        else -> transferColor
                    }.let { periodicDay.setBackgroundColor(it) }
                }
                is PeriodicDataByMonth -> {
                    periodicDay.visibility = View.GONE
                    periodicInfo.text = periodicData.month
                }
                is PeriodicDataByYear -> {
                    periodicDay.visibility = View.GONE
                    periodicInfo.text = periodicData.year
                }
            }
            periodicIncome.text = periodicData.totalIncome
            periodicExpense.text = periodicData.totalExpense
        }
    }


    abstract class ScrollListener(private val layoutManager: LinearLayoutManager) :
        RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount: Int = layoutManager.childCount
            val totalItemCount: Int = layoutManager.itemCount
            val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
            if (!isLoading() && !isLastPage()) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                    && firstVisibleItemPosition >= 0
                ) {
                    loadMoreItems()
                }
            }
        }

        protected abstract fun loadMoreItems()
        abstract fun isLastPage(): Boolean
        abstract fun isLoading(): Boolean
    }

}


