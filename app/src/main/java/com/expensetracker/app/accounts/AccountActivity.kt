package com.expensetracker.app.accounts

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.accounts.adapter.AccountsListAdapter
import com.expensetracker.app.accounts.support.AccountListData
import com.expensetracker.app.accounts.support.Literals.ACCOUNT_NAME_LABEL
import com.expensetracker.app.accounts.support.Literals.ACCOUNT_TYPE_LABEL
import com.expensetracker.app.accounts.viewmodels.AccountsViewModel
import com.expensetracker.app.databinding.ListingScreenBinding
import com.expensetracker.app.transactions.support.Literals.ACCOUNT_ID_LABEL
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard
import com.expensetracker.core.support.AccountType

class AccountActivity: AppCompatActivity() {

    private lateinit var binding: ListingScreenBinding
    private val viewModel: AccountsViewModel by viewModels<AccountsViewModel>()
    private val accountListData: MutableList<AccountListData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ListingScreenBinding.inflate(layoutInflater)

        binding.accountsScreenListView.layoutManager = LinearLayoutManager(this)

        val modifyAccountActivityLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                viewModel.fetchAccounts()
            }
        }

        binding.accountScreenBackBtn.setOnClickListener {
            finish()
        }

        binding.accountScreenEditBtn.setOnClickListener {
            viewModel.setRemovable(true)
        }

        binding.accountScreenDoneBtn.setOnClickListener {
            viewModel.setRemovable(false)
        }

        binding.accountScreenBackBtn.setOnClickListener {
            finish()
        }

        binding.accountScreenAddBtn.setOnClickListener {
            val intent = Intent(this, AccountAddActivity::class.java)
            modifyAccountActivityLauncher.launch(intent)
        }

        val removableAdapter = AccountsListAdapter(accountListData,true, onAccountRemoved = {
            viewModel.deleteAccount(it)
        })

        val listingAdapter = AccountsListAdapter(accountListData,false, onAccountClicked = { account ->
            val modifyIntent = Intent(this,AccountModifyActivity::class.java)
            modifyIntent.putExtra(ACCOUNT_ID_LABEL,account.id)
            modifyAccountActivityLauncher.launch(modifyIntent)
        })

        binding.accountsScreenListView.adapter = listingAdapter

        viewModel.getIsRemovable()

        viewModel.fetchAccounts()

        viewModel.isRemovable.observe(this, Observer {
            if(it){
                binding.accountScreenEditBtn.visibility = View.GONE
                binding.accountScreenDoneBtn.visibility = View.VISIBLE
                removableAdapter
            } else {
                binding.accountScreenEditBtn.visibility = View.VISIBLE
                binding.accountScreenDoneBtn.visibility = View.GONE
                listingAdapter
            }.let { adapter ->
                viewModel.accountListData.removeObservers(this)
                viewModel.accountListData.observe(this, Observer { list ->
                    accountListData.clear()
                    accountListData.addAll(list)
                    binding.accountsScreenListView.adapter = adapter
                    adapter.notifyDataSetChanged()
                })
            }
        })

        viewModel.total.observe(this, Observer {
            binding.accountInfo3.text = it.toString()
        })

        viewModel.liabilities.observe(this, Observer {
            binding.accountInfo2.text = it.toString()
        })



        setContentView(binding.root)

    }


}