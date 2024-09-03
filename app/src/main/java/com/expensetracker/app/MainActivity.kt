package com.expensetracker.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.accounts.AccountActivity
import com.expensetracker.app.accounts.AccountFragment
import com.expensetracker.app.category.SettingsActivity
import com.expensetracker.app.category.SettingsFragment
import com.expensetracker.app.databinding.AppMainBinding
import com.expensetracker.data_sqlite.DatabaseHelper
import com.expensetracker.data_sqlite.DatabaseSchema
import com.expensetracker.data_sqlite.services.accounts.BankAccountService
import com.expensetracker.data_sqlite.services.accounts.CashAccountService
import com.expensetracker.data_sqlite.services.accounts.CreditCardService
import com.expensetracker.data_sqlite.services.accounts.DebitCardService
import com.expensetracker.data_sqlite.services.category.ExpenseCategoryService
import com.expensetracker.data_sqlite.services.category.IncomeCategoryService
import com.expensetracker.data_sqlite.services.transactions.TransferService
import com.expensetracker.app.databinding.TransactionsScreenBinding
import com.expensetracker.app.support.DataGenerator
import com.expensetracker.app.transactions.fragment.TransactionsList
import com.expensetracker.app.transactions.viewmodel.TAG
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import com.expensetracker.core.actions.AccountActions
import com.expensetracker.core.actions.CategoryActions
import com.expensetracker.core.actions.TransactionActions
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.AccountResponse
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.CategoryResponse
import com.expensetracker.core.support.Helper
import com.expensetracker.core.support.SimpleName
import com.expensetracker.core.support.TransactionDescription
import com.expensetracker.core.support.TransactionResponse
import com.expensetracker.data_sqlite.services.support.IdGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: AppMainBinding
    private val mainViewModel: MainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = AppMainBinding.inflate(layoutInflater)

        val home = TransactionsList()
        val account = AccountFragment()
        val settings = SettingsFragment()

        updateFragment(mainViewModel.selectedFrag ?: home)

        //BOTTOM NAV BAR
        binding.appBottomNavBar.setOnItemSelectedListener {
            return@setOnItemSelectedListener when(it.itemId){
                R.id.home_nav_btn -> {
                    updateFragment(home)
                    true
                }
                R.id.account_nav_btn ->{
                    updateFragment(account)
                    true
                }
                R.id.settings_nav_btn -> {
                    updateFragment(settings)
                    true
                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback {
            if (mainViewModel.selectedFrag == home) finish()
            else binding.appBottomNavBar.selectedItemId = R.id.home_nav_btn
        }

        mainViewModel.isBackPressed.observe(this, Observer {
            if (mainViewModel.selectedFrag == home) finish()
            else binding.appBottomNavBar.selectedItemId = R.id.home_nav_btn
        })

        setContentView(binding.root)
    }

    private fun updateFragment(fragment: Fragment) {

        mainViewModel.selectedFrag = fragment

        val existingFrag = supportFragmentManager.findFragmentById(binding.appContainer.id)
        if(fragment == existingFrag) return

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.appContainer.id,fragment)

        if (existingFrag != null) transaction.addToBackStack(null)
        transaction.commit()
    }

}