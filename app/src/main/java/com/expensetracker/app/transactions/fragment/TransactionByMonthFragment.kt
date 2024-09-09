package com.expensetracker.app.transactions.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.databinding.TransactionListBinding
import com.expensetracker.app.transactions.adapter.TransactionsMonthListAdapter
import com.expensetracker.app.transactions.support.TransactionItemsByMonth
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel

class TransactionByMonthFragment : Fragment() {

    private lateinit var binding: TransactionListBinding
    private val viewModel: TransactionProviderViewModel by activityViewModels<TransactionProviderViewModel>()
    private val transactionItems: MutableList<TransactionItemsByMonth> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = TransactionListBinding.inflate(layoutInflater)

        val adapter = TransactionsMonthListAdapter(transactionItems) {

        }

        binding.theList.adapter = adapter
        binding.theList.layoutManager = LinearLayoutManager(requireContext())
        viewModel.prepareTransactionItemsByMonth(viewModel.year.value)

        viewModel.yearValue.observe(viewLifecycleOwner, Observer {
            viewModel.prepareTransactionItemsByMonth(it)
        })

        viewModel.transactionItemsByMonth.observe(viewLifecycleOwner, Observer {
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