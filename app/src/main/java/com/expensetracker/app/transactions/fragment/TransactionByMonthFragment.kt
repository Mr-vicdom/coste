package com.expensetracker.app.transactions.fragment

import android.os.Bundle
import android.util.Log
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
import com.expensetracker.app.transactions.viewmodel.TAG
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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


        viewModel.yearValue.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "year value test")
            viewModel.prepareTransactionItemsByMonth(it)
        })

        viewModel.transactionItemsByMonth.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "live data observed ${it.size}")
            transactionItems.clear()
            transactionItems.addAll(it)
            adapter.notifyDataSetChanged()
            binding.transScreenNothingFound.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.transScreenLoading.visibility = View.GONE
        })

        Log.d(TAG, "binding returned")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.prepareTransactionItemsByMonth(viewModel.year.value)
    }
}