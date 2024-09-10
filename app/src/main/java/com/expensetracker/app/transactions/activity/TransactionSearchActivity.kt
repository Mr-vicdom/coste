package com.expensetracker.app.transactions.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.R
import com.expensetracker.app.databinding.TransactionSearchScreenCoBinding
import com.expensetracker.app.transactions.adapter.TransactionsDayListAdapter
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_MONTH_LABEL
import com.expensetracker.app.transactions.support.SearchMode
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month
import java.util.Timer

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
                return true
            }

            var job: Job? = null
            var previousQuery: String = ""
            var lastQueried: String = ""
            var previousQueryResult: Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "onQueryTextChange: $newText")
                val query = newText?.trim(' ')
                if(query.isNullOrEmpty()){
                    Log.d(TAG, "onQueryTextChange: Empty ah?")
                    viewModel.fetchTransactionsMatches()
                } else {
                    previousQuery = query
                    if (job == null ) {
                        Log.d(TAG, "onQueryTextChange: Job assigned $query $previousQuery")
                        binding.transSearchLoading.visibility = View.VISIBLE
                        job = GlobalScope.launch {
                            viewModel.fetchTransactionsMatches(query = query)
                            delay(1000)
                            lastQueried = query
                            job = null
                            if (previousQuery != lastQueried) {
                                Log.d(TAG, "onQueryTextChanged: $previousQuery $lastQueried")
                                withContext(Dispatchers.Main) { onQueryTextChange(previousQuery) }
                            }
                        }
                    } else {
                        Log.d(TAG, "onQueryTextChange: Job rejected $query $previousQuery")
                    }
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


        binding.fieldDropDown.adapter = ArrayAdapter<String>(this, R.layout.search_dropdown_item,SearchMode.entries.map { it -> it.name.lowercase().replaceFirstChar { it.uppercase() } }.toMutableList())

        val fieldSelectedListener = SpinnerItemSelectedListener{ position ->
            val field = SearchMode.entries.getOrNull(position)
            field?.let {
                viewModel.searchMode = field
            }
        }

        binding.fieldDropDown.onItemSelectedListener = fieldSelectedListener

        viewModel.transactionItems.observe(this, Observer {
            binding.transSearchLoading.visibility = View.GONE
            binding.transSearchNothingFound.visibility = if (it.isNotEmpty())  View.GONE
            else View.VISIBLE
            listener.previousQueryResult = it.isNotEmpty()
            transactionItems.clear()
            transactionItems.addAll(it)
            adapter.notifyDataSetChanged()
        })

        setContentView(binding.root)
    }

}