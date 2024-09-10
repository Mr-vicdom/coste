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
import com.expensetracker.app.transactions.adapter.TransactionsWeekListAdapter
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.support.TransactionItemsByWeek
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
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

        viewModel.scrollToWeek.observe(viewLifecycleOwner, Observer {
            binding.theList.post {
                val selectedWeek = it
                val position =
                    transactionItems.indexOfFirst { (it is TransactionItemsByWeek.PeriodicItem) && (it.periodicData.weekNumber == selectedWeek) }
                if (position > 0) {
                    val childY : Float = binding.theList.y + (binding.theList.getChildAt(position)?.y ?: 0.0F)
                    viewModel.setScrollPosition(childY.toInt())
                    Log.d("=>log", "onCreate: Scroll to $selectedWeek $position")
                }
            }
        })

        return binding.root
    }

}