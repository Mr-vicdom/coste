package com.expensetracker.app.transactions.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.expensetracker.app.R
import com.expensetracker.app.data.DataHandler
import com.expensetracker.app.databinding.TransactionsScreenFragBinding
import com.expensetracker.app.transactions.activity.TransactionAddActivity
import com.expensetracker.app.transactions.activity.TransactionFilterActivity
import com.expensetracker.app.transactions.activity.TransactionModifyActivity
import com.expensetracker.app.transactions.activity.TransactionSearchActivity
import com.expensetracker.app.transactions.adapter.TransactionsDayListAdapter
import com.expensetracker.app.transactions.support.Literals.FILTER_ACCOUNT_IDS_LABEL
import com.expensetracker.app.transactions.support.Literals.MONTH_LABEL
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_MONTH_LABEL
import com.expensetracker.app.transactions.support.Literals.YEAR_LABEL
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.support.TransactionsViewMode
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.Transaction
import com.google.android.material.color.MaterialColors
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class TransactionsFragment: Fragment() {

    private val TAG = "TransFRag=>log"

    private val transactionProviderViewModel: TransactionProviderViewModel by activityViewModels<TransactionProviderViewModel>()
    private lateinit var binding: TransactionsScreenFragBinding

    private var transactionTotalIncome: Double = 0.0
    private var transactionTotalExpense: Double = 0.0
    private val transactionTotalBalance: Double
        get() = transactionTotalIncome - transactionTotalExpense

    private val transactionItems: MutableList<TransactionItems> = mutableListOf()

    private val filterAccounts: MutableList<AccountID> = mutableListOf()

    private fun updateFrag(fragment: Fragment){

        val existingFrag = childFragmentManager.findFragmentById(binding.transFragContainer.id)
        if(fragment == existingFrag) return

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.transFragContainer.id,fragment)

        transaction.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        savedInstanceState?.getIntArray(FILTER_ACCOUNT_IDS_LABEL)?.let {
            filterAccounts.clear()
            filterAccounts.addAll(it.toList())
        }

        binding = TransactionsScreenFragBinding.inflate(layoutInflater)


        val transactionScreenSearchBtn = binding.transScreenSearchBtn
        val transactionScreenCloseFilterBtn = binding.transScreenCloseFilterBtn
        val transScreenFilterBtn = binding.transScreenFilterBtn

        val addTransactionLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    transactionProviderViewModel.fetchTransactionsBetween(filterIDs = filterAccounts)
                }
            }

        val searchTransactionLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                transactionProviderViewModel.fetchTransactionsBetween()
            }

        val adapter = TransactionsDayListAdapter(transactionItems) { transaction: Transaction ->
            val modifyTransactionIntent = Intent(requireContext(), TransactionModifyActivity::class.java)
            modifyTransactionIntent.putExtra(TRANSACTION_ID_LABEL, transaction.id)
            addTransactionLauncher.launch(modifyTransactionIntent)
        }

        val filterTransactionLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "onCreate: Waited for filter...")
                    result.data?.getIntArrayExtra(FILTER_ACCOUNT_IDS_LABEL)?.let {
                        filterAccounts.clear()
                        filterAccounts.addAll(it.toList())
                        if (filterAccounts.isNotEmpty()) {
                            transactionScreenSearchBtn.visibility = View.GONE
                            transactionScreenCloseFilterBtn.visibility = View.VISIBLE
                        } else {
                            transactionScreenSearchBtn.visibility = View.VISIBLE
                            transactionScreenCloseFilterBtn.visibility = View.GONE
                        }
                        updateStatusBar()
                    }

                    transactionProviderViewModel.fetchTransactionsBetween(filterIDs = filterAccounts)
                }
            }

        //month pagination

        binding.transScreenMonth.text = transactionProviderViewModel.month.name.lowercase().replaceFirstChar {c -> c.uppercase() }

        binding.transPreviousBtn.setOnClickListener {
            with(transactionProviderViewModel){
                month = month.minus(1)
                fetchTransactionsBetween(filterAccounts)
            }
        }
        binding.transNextBtn.setOnClickListener {
            with(transactionProviderViewModel){
                month = month.plus(1)
                fetchTransactionsBetween(filterAccounts)
            }
        }

        transactionProviderViewModel.monthValue.observe(viewLifecycleOwner, Observer {
            binding.transScreenMonth.text = it.name.lowercase().replaceFirstChar {c -> c.uppercase() }
        })
        transactionProviderViewModel.yearValue.observe(viewLifecycleOwner, Observer {
            // Year impl
        })

        //Search BTN
        transactionScreenSearchBtn.setOnClickListener {
            val intent = Intent(requireContext(), TransactionSearchActivity::class.java)
            intent.putExtra(TRANSACTION_MONTH_LABEL, transactionProviderViewModel.month.value)
            searchTransactionLauncher.launch(intent)
        }

        if (filterAccounts.isNotEmpty()) {
            transactionScreenSearchBtn.visibility = View.GONE
            transactionScreenCloseFilterBtn.visibility = View.VISIBLE
            updateStatusBar()
        } else {
            transactionScreenSearchBtn.visibility = View.VISIBLE
            transactionScreenCloseFilterBtn.visibility = View.GONE
        }

        //Filter Cancel BTN
        transactionScreenCloseFilterBtn.setOnClickListener {
            filterAccounts.clear()
            //back status bar color
            transactionScreenSearchBtn.visibility = View.VISIBLE
            transactionScreenCloseFilterBtn.visibility = View.GONE
            transactionProviderViewModel.fetchTransactionsBetween()
            updateStatusBar()
        }

        //Filter BTN
        transScreenFilterBtn.setOnClickListener {
            val filterIntent = Intent(requireContext(), TransactionFilterActivity::class.java)
            filterIntent.putExtra(MONTH_LABEL, transactionProviderViewModel.month.value)
            filterIntent.putExtra(YEAR_LABEL, transactionProviderViewModel.year.value)
            filterIntent.putExtra(FILTER_ACCOUNT_IDS_LABEL, filterAccounts.toIntArray())
            filterTransactionLauncher.launch(filterIntent)
        }

        //Floating BTN
        binding.floatingBtn.setOnClickListener {
            val transAddIntent: Intent = Intent(requireContext(), TransactionAddActivity::class.java)
            addTransactionLauncher.launch(transAddIntent)
        }

        transactionProviderViewModel.totalIncome.observe(viewLifecycleOwner) {
            transactionTotalIncome = it
            binding.transInfo1.text = transactionTotalIncome.toString()
            binding.transInfo3.text = transactionTotalBalance.toString()
        }
        transactionProviderViewModel.totalExpense.observe(viewLifecycleOwner) {
            transactionTotalExpense = it
            binding.transInfo2.text = it.toString()
            binding.transInfo3.text = transactionTotalBalance.toString()
        }

        //Tab Layout

        var dailyTab : TabLayout.Tab? = null
        var weeklyTab : TabLayout.Tab? = null
        var monthlyTab : TabLayout.Tab? = null
        if (binding.transScreenViewBar.tabCount == 3){
            dailyTab = binding.transScreenViewBar.getTabAt(0)
            weeklyTab = binding.transScreenViewBar.getTabAt(1)
            monthlyTab = binding.transScreenViewBar.getTabAt(2)
        }
        binding.transScreenViewBar.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(binding.transScreenViewBar.tabCount == 3) {
                    val position = tab?.let { if (it.position < 3) it.position else 1 } ?: 1
                    TransactionsViewMode.entries.getOrNull(position)?.let {
                        transactionProviderViewModel.setTransactionsViewMode(it)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        transactionProviderViewModel.getTransactionsViewMode()


        //Transactions Frag
        transactionProviderViewModel.transactionsViewMode.observe(viewLifecycleOwner, Observer { transactionViewMode: TransactionsViewMode ->

            val dayFragment = TransactionByDayFragment()
            val weekFragment = TransactionByWeekFragment()

            when(transactionViewMode){
                TransactionsViewMode.DAILY -> {
                    binding.transScreenViewBar.selectTab(dailyTab)
                    dayFragment
                }
                TransactionsViewMode.WEEKLY -> {
                    binding.transScreenViewBar.selectTab(weeklyTab)
                    weekFragment
                }
                TransactionsViewMode.MONTHLY -> {
                    binding.transScreenViewBar.selectTab(dailyTab)
                    dayFragment
                }
            }.let {
                updateFrag(it)
            }

        })

        transactionProviderViewModel.transactionItems.observe(viewLifecycleOwner, Observer {
            transactionItems.clear()
            transactionItems.addAll(it)
            adapter.notifyDataSetChanged()
        })

        return binding.root
    }

    private fun updateStatusBar() {
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        var color =
            MaterialColors.getColor(requireContext(), androidx.appcompat.R.attr.colorPrimary, colorPrimary)
            requireActivity().window.statusBarColor = color
        if (filterAccounts.isNotEmpty()) {
            color = ContextCompat.getColor(requireContext(), R.color.dark_blue_600)
            requireActivity().window.statusBarColor = color
        }
        binding.transScreenAppBar.setBackgroundColor(color)
        binding.transScreenViewBar.setBackgroundColor(color)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putIntArray(FILTER_ACCOUNT_IDS_LABEL, filterAccounts.toIntArray())
    }

}
