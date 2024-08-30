package com.expensetracker.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.app.databinding.TransactionsScreenBinding
import com.expensetracker.app.transactions.fragment.TransactionsList

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val binding = TransactionsScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

    }

}