package com.expensetracker.app.transactions.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.databinding.TransactionFilterFieldBinding
import com.expensetracker.app.databinding.TransactionFilterFieldTitleBinding
import com.expensetracker.app.transactions.support.TransactionFilterData
import com.expensetracker.app.views.CurrencyTextView
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.AccountID

const val FILTER_TITLE_TYPE = 0
const val FILTER_ITEM_TYPE = 1

class TransactionsFilterAdapter(
    private val transactionFilterData: MutableList<TransactionFilterData>,
    private val selectedAccounts: Set<AccountID> = setOf(),
    private val checkBoxes: MutableMap<AccountID,CheckBox> = mutableMapOf(),
    private val onFilterApplied: (TransactionFilterData.TransactionFilterItem) -> Unit = {},
    private val onFilterRemoved: (TransactionFilterData.TransactionFilterItem) -> Unit = {}
): RecyclerView.Adapter<TransactionsFilterAdapter.FilterViewHolder>() {

    override fun onViewAttachedToWindow(holder: FilterViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when(viewType){
            FILTER_TITLE_TYPE -> {
                val binding = TransactionFilterFieldTitleBinding.inflate(layoutInflater,parent,false)
                FilterTitleViewHolder(binding)
            }
            FILTER_ITEM_TYPE -> {
                val binding = TransactionFilterFieldBinding.inflate(layoutInflater,parent,false)
                FilterItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE)
        }
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val data = transactionFilterData[position]
        when(holder){
            is FilterItemViewHolder -> {
                if(data is TransactionFilterData.TransactionFilterItem)
                    holder.bind(data)
            }
            is FilterTitleViewHolder -> {
                if (data is TransactionFilterData.TransactionFilterTitle)
                    holder.bind(data)
            }
        }
    }

    override fun getItemCount(): Int {
        return transactionFilterData.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(transactionFilterData[position]) {
            is TransactionFilterData.TransactionFilterItem -> FILTER_ITEM_TYPE
            is TransactionFilterData.TransactionFilterTitle -> FILTER_TITLE_TYPE
        }
    }

    sealed class FilterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    inner class FilterTitleViewHolder(binding: TransactionFilterFieldTitleBinding): FilterViewHolder(binding.root) {
        private val title: TextView = binding.transFilterFieldTitle
        fun bind(data: TransactionFilterData.TransactionFilterTitle){
            title.text = data.title
        }
    }

    inner class FilterItemViewHolder(binding: TransactionFilterFieldBinding): FilterViewHolder(binding.root) {
        val checkBox: CheckBox = binding.transFilterField
        val label: TextView = binding.transFilterFieldLabel
        val income: CurrencyTextView = binding.transFilterFieldIncome
        val expense: CurrencyTextView = binding.transFilterFieldExpense
        val transferIn: CurrencyTextView = binding.transFilterFieldTransferIn
        val transferOut: CurrencyTextView = binding.transFilterFieldTransferOut
        fun bind(data: TransactionFilterData.TransactionFilterItem) {
            checkBox.isChecked = selectedAccounts.contains(data.accountID)
            label.text = data.label
            income.text = data.income.toString()
            expense.text = data.expense.toString()
            transferIn.text = data.transferIn.toString()
            transferOut.text = data.transferOut.toString()

            checkBoxes[data.accountID] = checkBox

            itemView.setOnClickListener {
                if(checkBox.isChecked){
                    checkBox.isChecked = false
                    onFilterRemoved(data)
                } else {
                    checkBox.isChecked = true
                    onFilterApplied(data)
                }
            }
        }
    }
}