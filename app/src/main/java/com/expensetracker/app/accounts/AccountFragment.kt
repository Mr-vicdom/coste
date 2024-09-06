package com.expensetracker.app.accounts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.MainViewModel
import com.expensetracker.app.R
import com.expensetracker.app.accounts.adapter.AccountsListAdapter
import com.expensetracker.app.accounts.support.AccountListData
import com.expensetracker.app.accounts.viewmodels.AccountsViewModel
import com.expensetracker.app.databinding.AccountListingScreenBinding
import com.expensetracker.app.transactions.support.Literals.ACCOUNT_ID_LABEL
import com.expensetracker.app.transactions.support.getChoiceAlertDialog
import com.google.android.material.color.MaterialColors

class AccountFragment: Fragment() {

    private lateinit var binding: AccountListingScreenBinding
    private val viewModel: AccountsViewModel by activityViewModels<AccountsViewModel>()
    private val mainViewModel: MainViewModel by activityViewModels<MainViewModel>()
    private val accountListData: MutableList<AccountListData> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        val color = MaterialColors.getColor(requireContext(), androidx.appcompat.R.attr.colorPrimary, colorPrimary)
        requireActivity().window.statusBarColor = color

        binding = AccountListingScreenBinding.inflate(layoutInflater)

        binding.accountsScreenListView.layoutManager = LinearLayoutManager(requireContext())

        val modifyAccountActivityLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                viewModel.fetchAccounts()
            }
        }


        binding.accountScreenEditBtn.setOnClickListener {
            viewModel.setRemovable(true)
        }

        binding.accountScreenDoneBtn.setOnClickListener {
            viewModel.setRemovable(false)
        }


        binding.accountScreenAddBtn.setOnClickListener {
            val intent = Intent(requireContext(), AccountAddActivity::class.java)
            modifyAccountActivityLauncher.launch(intent)
        }

        val removableAdapter = AccountsListAdapter(accountListData,true, onAccountRemoved = {
            getChoiceAlertDialog(requireContext(),"Delete Account?","Results deleting linked transactions!!", onYesClick = {
                viewModel.deleteAccount(it)
            }).show()
        })

        val listingAdapter = AccountsListAdapter(accountListData,false, onAccountClicked = { account ->
            val modifyIntent = Intent(requireContext(),AccountModifyActivity::class.java)
            Log.d("=>log", "onClicked: $account")
            modifyIntent.putExtra(ACCOUNT_ID_LABEL,account.id)
            modifyAccountActivityLauncher.launch(modifyIntent)
        })

        binding.accountsScreenListView.adapter = listingAdapter

        viewModel.getIsRemovable()

        viewModel.fetchAccounts()

        viewModel.isRemovable.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.accountScreenEditBtn.visibility = View.GONE
                binding.accountScreenDoneBtn.visibility = View.VISIBLE
                removableAdapter
            } else {
                binding.accountScreenEditBtn.visibility = View.VISIBLE
                binding.accountScreenDoneBtn.visibility = View.GONE
                listingAdapter
            }.let { adapter ->
                viewModel.accountListData.removeObservers(viewLifecycleOwner)
                viewModel.accountListData.observe(viewLifecycleOwner, Observer { list ->
                    accountListData.clear()
                    accountListData.addAll(list)
                    binding.accountsScreenListView.adapter = adapter
                    adapter.notifyDataSetChanged()
                })
            }
        })

        viewModel.total.observe(viewLifecycleOwner, Observer {
            binding.accountInfo3.text = it.toString()
        })

        viewModel.liabilities.observe(viewLifecycleOwner, Observer {
            binding.accountInfo2.text = it.toString()
        })


        requireActivity().onBackPressedDispatcher.addCallback {
            mainViewModel.postBackPressed()
        }

        return binding.root
    }
}