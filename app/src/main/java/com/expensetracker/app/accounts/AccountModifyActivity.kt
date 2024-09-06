package com.expensetracker.app.accounts

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.expensetracker.app.accounts.support.Literals.ACCOUNT_NAME_LABEL
import com.expensetracker.app.accounts.support.Literals.ACCOUNT_TYPE_LABEL
import com.expensetracker.app.transactions.support.Literals.ACCOUNT_ID_LABEL
import com.expensetracker.app.transactions.support.SURETY
import com.expensetracker.app.transactions.support.getChoiceAlertDialog
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard
import com.expensetracker.core.support.AccountType

class AccountModifyActivity: AccountAddActivity() {

    val TAG = "AccountModify=>"

    private var oldAccount : Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { extras->
            val id = extras.getIntExtra(ACCOUNT_ID_LABEL,-1)
            if (id == -1){
                setResult(RESULT_CANCELED)
                finish()
            } else {
               viewModel.getAccount(id)
            }
        }

        viewModel.updateAccount.observe(this, Observer { account ->
            if (account == null) {
                setResult(RESULT_CANCELED)
                finish()
                return@Observer
            }

            oldAccount = account
            binding.nameField.setText(account.name.toString())
            when(account){
                is CreditCard -> AccountType.CREDIT_CARD
                is DebitCard -> AccountType.DEBIT_CARD
                is BankAccount -> AccountType.BANK_ACCOUNT
                is CashAccount -> AccountType.CASH_ACCOUNT
            }.let {
                binding.groupField.setSelection(it.ordinal)
                groupType = it

                if (it == AccountType.DEBIT_CARD && account is DebitCard){

                    val observer = Observer<List<BankAccount>> { bankAccounts ->
                        binding.bankAccountsField.setSelection(bankAccounts.map { ac -> ac.id }.indexOf(account.bankAccount.id))
                        Log.d("=>log", "onCreate: $bankAccounts")
                    }

                    viewModel.bankAccounts.observe(this,observer)
                }

            }
        })

        binding.saveBtn.setOnClickListener {
            val selectedGroupType = groupType
            val account = oldAccount
            if (account == null){
                Toast.makeText(this, "Account Not Found", Toast.LENGTH_SHORT).show()
                setResult(RESULT_CANCELED)
                finish()
                return@setOnClickListener
            }
            val name = binding.nameField.text.toString()
            val isSameAc = when(account){
                is CreditCard -> selectedGroupType == AccountType.CREDIT_CARD
                is DebitCard -> selectedGroupType == AccountType.DEBIT_CARD
                is BankAccount -> selectedGroupType == AccountType.BANK_ACCOUNT
                is CashAccount -> selectedGroupType == AccountType.CASH_ACCOUNT
            }
            val isSameName = account.name.toString() == name
            if (isSameName && isSameAc) {
                Toast.makeText(this, "No Changes Found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            getChoiceAlertDialog(this,"Update Account",SURETY, onYesClick = {
                val bankAccount: BankAccount? =
                    if (bankAccounts.contains(selectedBankAccount)) bankAccounts[selectedBankAccount] else null

                setResult(RESULT_OK)
                when(account){
                    is CreditCard -> viewModel.updateCreditCard(account,selectedGroupType,name,bankAccount)
                    is DebitCard -> viewModel.updateDebitCard(account,selectedGroupType,name,bankAccount)
                    is BankAccount -> viewModel.updateBankAccount(account,selectedGroupType,name,bankAccount)
                    is CashAccount -> viewModel.updateCashAccount(account,selectedGroupType,name,bankAccount)
                }
                finish()
            }).show()
        }
    }

}