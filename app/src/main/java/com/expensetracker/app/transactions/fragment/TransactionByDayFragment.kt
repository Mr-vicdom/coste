package com.expensetracker.app.transactions.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.databinding.TransactionListBinding
import com.expensetracker.app.transactions.activity.TransactionModifyActivity
import com.expensetracker.app.transactions.adapter.TransactionsDayListAdapter
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import com.expensetracker.core.models.Transaction

class TransactionByDayFragment: Fragment() {

    private lateinit var binding: TransactionListBinding
    private val viewModel: TransactionProviderViewModel by activityViewModels<TransactionProviderViewModel>()
    private val transactionItems: MutableList<TransactionItems> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TransactionListBinding.inflate(layoutInflater)

        val modifyTransactionLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    viewModel.fetchTransactionsBetween()
                }
            }

        val adapter = TransactionsDayListAdapter(transactionItems){transaction: Transaction ->
            val modifyTransactionIntent = Intent(requireContext(), TransactionModifyActivity::class.java)
            modifyTransactionIntent.putExtra(TRANSACTION_ID_LABEL, transaction.id)
            modifyTransactionLauncher.launch(modifyTransactionIntent)
        }

        binding.theList.adapter = adapter
        binding.theList.layoutManager = LinearLayoutManager(requireContext())
        binding.theList.post {
            viewModel.fetchTransactionsBetween()
        }

        viewModel.transactionItems.observe(this, Observer {
            transactionItems.clear()
            transactionItems.addAll(it)
            adapter.notifyDataSetChanged()
            binding.transScreenNothingFound.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.transScreenLoading.visibility = View.GONE
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}