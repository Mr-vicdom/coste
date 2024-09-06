package com.expensetracker.app.accounts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.expensetracker.app.R
import com.expensetracker.app.accounts.viewmodels.AccountsViewModel
import com.expensetracker.app.databinding.AccountAddScreenBinding
import com.expensetracker.app.transactions.activity.SpinnerItemSelectedListener
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.DebitCardID
import com.expensetracker.core.support.AccountType

open class AccountAddActivity : AppCompatActivity() {

    protected lateinit var binding: AccountAddScreenBinding
    protected val viewModel: AccountsViewModel by viewModels<AccountsViewModel>()
    protected var groupType: AccountType = AccountType.BANK_ACCOUNT
    protected val bankAccounts: MutableMap<Int, BankAccount> = mutableMapOf()
    protected val bankAccountAsItems: MutableList<String>
        get() = bankAccounts.values.map { it.name.toString() }.toMutableList()
    protected var selectedBankAccount: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AccountAddScreenBinding.inflate(layoutInflater)

        val accountTypes: Map<AccountType, String> = mapOf(
            AccountType.BANK_ACCOUNT to "BankAccount",
            AccountType.CASH_ACCOUNT to "CashAccount",
            AccountType.CREDIT_CARD to "CreditCard",
            AccountType.DEBIT_CARD to "DebitCard"
        )
        val typesOfAccounts: MutableList<String> = accountTypes.values.toMutableList()

        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_item, typesOfAccounts)
        val bankAccountAdapter =
            ArrayAdapter<String>(this, R.layout.dropdown_item, bankAccountAsItems)

        val groupField: Spinner = binding.groupField
        val bankAccountLabel: TextView = binding.bankAccountsLabel
        val bankAccountField: Spinner = binding.bankAccountsField
        val nameField: EditText = binding.nameField
        val saveBtn: Button = binding.saveBtn


        val groupSelectedListener = SpinnerItemSelectedListener { position ->
            if (accountTypes.keys.toList().indices.contains(position))
                groupType = accountTypes.keys.toList().elementAt(position)
            if (groupType == AccountType.DEBIT_CARD) {
                viewModel.getBankAccounts()
                bankAccountLabel.visibility = View.VISIBLE
                bankAccountField.visibility = View.VISIBLE
            } else {
                bankAccountLabel.visibility = View.GONE
                bankAccountField.visibility = View.GONE
            }
        }

        val bankSelectedListener = SpinnerItemSelectedListener { position ->
            if(bankAccounts.isNotEmpty() && bankAccounts.contains(position))
            selectedBankAccount = bankAccounts.keys.toList()[position]
        }

        groupField.onItemSelectedListener = groupSelectedListener
        bankAccountField.onItemSelectedListener = bankSelectedListener

        groupField.adapter = adapter
        bankAccountField.adapter = bankAccountAdapter

        //Back btn
        binding.accountAddBackBtn.setOnClickListener{
            setResult(RESULT_CANCELED)
            finish()
        }

        // fetched bank accounts
        viewModel.bankAccounts.observe(this, Observer { accounts ->
            bankAccounts.clear()
            bankAccounts.putAll(accounts.associateBy { it.id })
            selectedBankAccount = bankAccounts.keys.firstOrNull()
            bankAccountAdapter.clear()
            bankAccountAdapter.addAll(bankAccountAsItems)
            bankAccountAdapter.notifyDataSetChanged()
        })


        //Save btn
        saveBtn.setOnClickListener {
            val name = nameField.text.toString()
            if (name.isNotEmpty()) {
                viewModel.createAccount(groupType, name,
                    selectedBankAccount?.let {
                        if (bankAccounts.contains(it))
                            bankAccounts[it]
                        else null
                })
                Log.d("=>log", "onCreate: $selectedBankAccount $bankAccounts")
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show()
            }
        }

        setContentView(binding.root)
    }

}