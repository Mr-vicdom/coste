package com.expensetracker.app.accounts.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.accounts.support.AccountListData
import com.expensetracker.app.databinding.AccountListItemBinding
import com.expensetracker.app.databinding.ListItemsTitleBinding
import com.expensetracker.app.databinding.RemovableItemBinding
import com.expensetracker.app.transactions.adapter.INVALID_VIEW_TYPE
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.CreditCard


const val ACCOUNT_TITLE_TYPE = 0
const val ACCOUNT_ITEM_TYPE = 1

class AccountsListAdapter(
    private val accountListData: MutableList<AccountListData>,
    private val isRemovable: Boolean = false,
    private val onAccountClicked: (Account) -> Unit = {},
    private val onAccountRemoved: (Account) -> Unit = {}
): RecyclerView.Adapter<AccountsListAdapter.AccountListingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountListingViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when(viewType){
            ACCOUNT_TITLE_TYPE -> {
                val binding = ListItemsTitleBinding.inflate(layoutInflater,parent,false)
                AccountTitleViewHolder(binding)
            }
            ACCOUNT_ITEM_TYPE -> {
                if(isRemovable){
                    val binding = RemovableItemBinding.inflate(layoutInflater,parent,false)
                    AccountRemovableItemViewHolder(binding)
                } else {
                    val binding = AccountListItemBinding.inflate(layoutInflater,parent,false)
                    AccountItemViewHolder(binding)
                }

            }
            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE)
        }
    }

    override fun onBindViewHolder(holder: AccountListingViewHolder, position: Int) {
        val data = accountListData[position]
        when(holder){
            is AccountTitleViewHolder -> {
                if(data is AccountListData.AccountTitle)
                    holder.bind(data)
            }
            is AccountItemViewHolder -> {
                if (data is AccountListData.AccountItem)
                    holder.bind(data)
            }
            is AccountRemovableItemViewHolder -> {
                if (data is AccountListData.AccountItem)
                    holder.bind(data)
            }
        }
    }

    override fun getItemCount(): Int = accountListData.size

    override fun getItemViewType(position: Int): Int {
        return when(accountListData[position]){
            is AccountListData.AccountItem -> ACCOUNT_ITEM_TYPE
            is AccountListData.AccountTitle -> ACCOUNT_TITLE_TYPE
        }
    }

    sealed class AccountListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class AccountTitleViewHolder(binding: ListItemsTitleBinding): AccountListingViewHolder(binding.root){
        val title = binding.listItemTitle
        fun bind(data: AccountListData.AccountTitle) {
            title.text = data.title
        }
    }

    inner class AccountItemViewHolder(private val binding: AccountListItemBinding): AccountListingViewHolder(binding.root){
        val name = binding.accountName
        val balance = binding.accountBalance
        fun bind(data: AccountListData.AccountItem) {
            name.text = data.account.name.toString()
            if(data.account is CreditCard)
                balance.text = data.account.outStandings.toString()
            else
                balance.text = data.account.balance.toString()

            Log.d("=>log", "bind: $data")
            binding.root.setOnClickListener {
                onAccountClicked(data.account)
            }
        }
    }

    inner class AccountRemovableItemViewHolder(private val binding: RemovableItemBinding): AccountListingViewHolder(binding.root){
        val name = binding.fieldText
        fun bind(data: AccountListData.AccountItem) {
            name.text = data.account.name.toString()

            binding.removeIcon.setOnClickListener {
                onAccountRemoved(data.account)
            }
        }
    }
}