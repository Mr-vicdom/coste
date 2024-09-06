package com.expensetracker.app

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.expensetracker.app.accounts.AccountFragment
import com.expensetracker.app.category.SettingsFragment
import com.expensetracker.app.databinding.AppMainBinding
import com.expensetracker.app.transactions.fragment.TransactionsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: AppMainBinding
    private val mainViewModel: MainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = AppMainBinding.inflate(layoutInflater)

        val home = TransactionsFragment()
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

        transaction.commit()
    }

}