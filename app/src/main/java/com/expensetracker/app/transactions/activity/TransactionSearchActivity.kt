package com.expensetracker.app.transactions.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.databinding.TransactionSearchScreenCoBinding
import com.expensetracker.app.transactions.adapter.TransactionsDayListAdapter
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_MONTH_LABEL
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import java.time.LocalDate
import java.time.Month

class TransactionSearchActivity: AppCompatActivity() {

    private val TAG = "TransSearch=>log"
    private val viewModel : TransactionProviderViewModel by viewModels<TransactionProviderViewModel>()
    private val transactionItems: MutableList<TransactionItems> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { intent ->
            intent.getIntExtra(TRANSACTION_MONTH_LABEL,LocalDate.now().monthValue).let {
                try {
                    viewModel.month = Month.of(it)
                }catch (_: Exception){}
            }
        }

        val binding = TransactionSearchScreenCoBinding.inflate(layoutInflater)

        val searchView: SearchView = binding.transSearchView

        val listener = object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@TransactionSearchActivity, "$query", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "onQueryTextChange: $newText")
                if(newText.isNullOrEmpty()){
                    viewModel.fetchTransactionsMatches()
                } else {
                    binding.transSearchLoading.visibility = View.VISIBLE
                    viewModel.fetchTransactionsMatches(query = newText)
                }
                return true
            }

        }

        binding.transSearchCloseBtn.setOnClickListener {
            finish()
        }

        searchView.setOnQueryTextListener(listener)

        val modifyActivityLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK){
                viewModel.fetchTransactionsMatches(query = searchView.query.toString())
            }
        }

        val adapter = TransactionsDayListAdapter(transactionItems) {
            val modifyTransactionIntent = Intent(this,TransactionModifyActivity::class.java)
            modifyTransactionIntent.putExtra(TRANSACTION_ID_LABEL,it.id)
            modifyActivityLauncher.launch(modifyTransactionIntent)
        }

        binding.transSearchResult.adapter = adapter

        binding.transSearchResult.layoutManager = LinearLayoutManager(this)

        viewModel.transactionItems.observe(this, Observer {
            binding.transSearchLoading.visibility = View.GONE
            binding.transSearchNothingFound.visibility = if (it.isNotEmpty())  View.GONE
            else View.VISIBLE
            transactionItems.clear()
            transactionItems.addAll(it)
            adapter.notifyDataSetChanged()
        })

        setContentView(binding.root)
    }

}