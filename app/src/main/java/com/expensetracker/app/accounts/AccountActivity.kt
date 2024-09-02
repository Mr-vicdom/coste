package com.expensetracker.app.accounts

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.accounts.viewmodels.AccountsViewModel
import com.expensetracker.app.databinding.ListingScreenBinding

class AccountActivity: AppCompatActivity() {

    private lateinit var binding: ListingScreenBinding
    private val viewModel: AccountsViewModel by viewModels<AccountsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ListingScreenBinding.inflate(layoutInflater)

        binding.accountsScreenListView.layoutManager = LinearLayoutManager(this)

        binding.accountScreenBackBtn.setOnClickListener {
            finish()
        }

        binding.accountScreenEditBtn.setOnClickListener {
            viewModel.setRemovable(true)
        }

        binding.accountScreenDoneBtn.setOnClickListener {
            viewModel.setRemovable(false)
        }

        viewModel.getIsRemovable()

        viewModel.getAdapter()

        viewModel.isRemovable.observe(this, Observer {
            if(it){
                binding.accountScreenEditBtn.visibility = View.GONE
                binding.accountScreenDoneBtn.visibility = View.VISIBLE
            } else {
                binding.accountScreenEditBtn.visibility = View.VISIBLE
                binding.accountScreenDoneBtn.visibility = View.GONE
            }
        })

        viewModel.listAdapter.observe(this, Observer {
            binding.accountsScreenListView.adapter = it
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