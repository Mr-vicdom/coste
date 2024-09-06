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
import com.expensetracker.app.transactions.adapter.TransactionsWeekListAdapter
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.support.TransactionItemsByWeek
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import com.expensetracker.core.models.Transaction
import java.time.LocalDate

class TransactionByWeekFragment : Fragment() {

    private lateinit var binding: TransactionListBinding
    private val viewModel: TransactionProviderViewModel by activityViewModels<TransactionProviderViewModel>()
    private val transactionItems: MutableList<TransactionItemsByWeek> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = TransactionListBinding.inflate(layoutInflater)

        val adapter = TransactionsWeekListAdapter(transactionItems) { date: LocalDate ->
            viewModel.setScrollPosition(date)
        }

        binding.theList.adapter = adapter
        binding.theList.layoutManager = LinearLayoutManager(requireContext())
        binding.theList.post {
            viewModel.fetchTransactionsBetween()
        }

        viewModel.transactionItemsByWeek.observe(viewLifecycleOwner, Observer {
            transactionItems.clear()
            transactionItems.addAll(it)
            adapter.notifyDataSetChanged()
            binding.transScreenNothingFound.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.transScreenLoading.visibility = View.GONE
        })

        return binding.root
    }

}