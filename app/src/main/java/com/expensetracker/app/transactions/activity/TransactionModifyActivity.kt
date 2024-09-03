package com.expensetracker.app.transactions.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.support.getAlertDialog
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.FinancialTransaction
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.Helper
import com.expensetracker.core.support.TransactionType

const val NO_TRANSACTION_FOUND = "No Transaction Found "
const val NO_CHANGES_FOUND = "No Changes Found "
const val CHANGED = true
const val UNCHANGED = false

class TransactionModifyActivity: TransactionAddActivity() {
    private var id: Int = -1
    private var transaction: Transaction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null) {
            id = intent.getIntExtra(TRANSACTION_ID_LABEL, id)
            if(id != -1){
                transactionManagerViewModel.fetchTransaction(id)
            }
        }

        val dateField: TextView = binding.dateField
        val amountField: EditText = binding.amountField
        val categoryField: Spinner = binding.categoryField
        val accountField: Spinner = binding.accountField
        val noteField: EditText = binding.noteField
        val descriptionField: EditText = binding.descriptionField
        val saveBtn: Button = binding.saveBtn
        val deleteBtn: Button = binding.deleteBtn

        transactionManagerViewModel.existingTransaction.observe(this, Observer { fetchedTransaction: Transaction? ->
            if (fetchedTransaction != null) {
                transaction = fetchedTransaction
                date = Helper.millisToDate(fetchedTransaction.date)
                amount = fetchedTransaction.amount.toString()
                note = fetchedTransaction.note.toString()
                description = fetchedTransaction.description.toString()
                when (fetchedTransaction) {
                    is FinancialTransaction -> {
                        categoryID = fetchedTransaction.category.id
                        accountID = fetchedTransaction.account.id
                    }

                    is Transfer -> {
                        categoryID = fetchedTransaction.fromAccount.id
                        accountID = fetchedTransaction.toAccount.id
                    }
                }
                val categoryPosition = categoryMap.keys.indexOf(categoryID)
                if (categoryPosition != -1) categoryField.setSelection(categoryPosition)
                val accountPosition = accountMap.keys.indexOf(accountID)
                if (accountPosition != -1) accountField.setSelection(accountPosition)


                if(binding.transAddTabBar.tabCount == 3){
                    val tab1 = binding.transAddTabBar.getTabAt(0)
                    val tab2 = binding.transAddTabBar.getTabAt(1)
                    val tab3 = binding.transAddTabBar.getTabAt(2)
                    when(transactionType){
                        TransactionType.INCOME -> tab1
                        TransactionType.EXPENSE -> tab2
                        TransactionType.TRANSFER -> tab3
                    }.let { binding.transAddTabBar.selectTab(it) }
                }

                dateField.text = date.toString()
                amountField.setText(amount)
                noteField.setText(note)
                descriptionField.setText(description)
                deleteBtn.visibility = View.VISIBLE

                deleteBtn.setOnClickListener {
                    val onYesClickListener = {
                        transactionManagerViewModel.deleteTransaction(fetchedTransaction)
                        setResult(RESULT_OK)
                        finish()
                    }
                    getAlertDialog(this,"Delete","Are You Sure?", onYesClick = onYesClickListener).show()
                }

                saveBtn.setOnClickListener {

                    amount = amountField.text.toString()
                    note = noteField.text.toString()
                    description = descriptionField.text.toString()
                    if(isTransactionChanged()){

                        val onYesClickListener = {


                            if ((amount.toDoubleOrNull() ?: 0.0) <= 0.0 ){
                                Toast.makeText(this, "Amount can't be <=0", Toast.LENGTH_SHORT).show()
                            } else {

                                when (transactionType) {
                                    TransactionType.INCOME -> transactionManagerViewModel.updateIncome(
                                        fetchedTransaction,
                                        date,
                                        amount,
                                        note,
                                        description,
                                        categoryID,
                                        accountID
                                    )

                                    TransactionType.EXPENSE -> transactionManagerViewModel.updateExpense(
                                        fetchedTransaction,
                                        date,
                                        amount,
                                        note,
                                        description,
                                        categoryID,
                                        accountID
                                    )

                                    TransactionType.TRANSFER -> {
                                        if (accountID != categoryID) transactionManagerViewModel.updateTransfer(
                                            fetchedTransaction,
                                            date,
                                            amount,
                                            note,
                                            description,
                                            fromAccountID = categoryID,
                                            toAccountID = accountID
                                        )
                                        else {
                                            Toast.makeText(
                                                this,
                                                "Can't transfer on same A/C",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                            setResult(RESULT_OK)
                            finish()

                        }
                        getAlertDialog(this,"Update","Are You Sure?", onYesClick = onYesClickListener).show()

                    } else {
                        Toast.makeText(this, NO_CHANGES_FOUND, Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, NO_TRANSACTION_FOUND, Toast.LENGTH_SHORT).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        })

    }

    override fun onBackClicked(): Boolean {
        if(isTransactionChanged()){

            val onYesClickListener = {
                setResult(RESULT_OK)
                finish()
            }
            getAlertDialog(this,"Discard Changes","Are You Sure?", onYesClick = onYesClickListener).show()
            return false
        } else {
            setResult(RESULT_OK)
            finish()
            return true
        }
    }

    private fun isTransactionChanged(): Boolean {
        val oldTransaction = transaction ?: return UNCHANGED
        Log.d(TAG, "onBackClicked: \" $id == ${oldTransaction.id} $amount == ${oldTransaction.amount} $note == ${oldTransaction.note} $description == ${oldTransaction.description}\"")
        return if(Helper.dateToMillis(date) == oldTransaction.date &&
                amount == oldTransaction.amount.toString() &&
                note == oldTransaction.note.toString() &&
                description == oldTransaction.description.toString()){
            when(oldTransaction){
                is FinancialTransaction -> {
                    Log.d(TAG, "onBackClicked: \"$categoryID == ${oldTransaction.category.id} $accountID == ${oldTransaction.account.id}\" $transactionType")
                    (categoryID != oldTransaction.category.id ||
                    accountID != oldTransaction.account.id) ||
                        when(oldTransaction){
                            is Expense -> transactionType != TransactionType.EXPENSE
                            is Income -> transactionType != TransactionType.INCOME
                        }
                }
                is Transfer -> {
                    Log.d(TAG, "onBackClicked: \"$categoryID == ${oldTransaction.fromAccount.id} $accountID == ${oldTransaction.toAccount.id}\" $transactionType")
                    (categoryID != oldTransaction.fromAccount.id ||
                        accountID != oldTransaction.toAccount.id ||
                        transactionType != TransactionType.TRANSFER)
                }
            }
        } else CHANGED
    }
}