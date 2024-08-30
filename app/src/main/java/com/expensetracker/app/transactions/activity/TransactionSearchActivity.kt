package com.expensetracker.app.transactions.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.databinding.TransactionSearchScreenBinding
import com.expensetracker.app.transactions.adapter.TransactionSearchAdapter
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel

class TransactionSearchActivity: AppCompatActivity() {

    val viewModel : TransactionProviderViewModel by viewModels<TransactionProviderViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = TransactionSearchScreenBinding.inflate(layoutInflater)

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

        binding.transSearchResult.adapter = viewModel.getTransactionListAdapter {
            val modifyTransactionIntent = Intent(this,TransactionModifyActivity::class.java)
            modifyTransactionIntent.putExtra(TRANSACTION_ID_LABEL,it.id)
            modifyActivityLauncher.launch(modifyTransactionIntent)
        }

        binding.transSearchResult.layoutManager = LinearLayoutManager(this)

        setContentView(binding.root)
    }

}