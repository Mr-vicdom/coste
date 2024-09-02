package com.expensetracker.data_sqlite.services.transactions

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.expensetracker.core.actions.BankAccountActions
import com.expensetracker.core.actions.CashAccountActions
import com.expensetracker.core.actions.CreditCardActions
import com.expensetracker.core.actions.DebitCardActions
import com.expensetracker.core.actions.IncomeActions
import com.expensetracker.core.actions.IncomeCategoryActions
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.TransactionDate
import com.expensetracker.core.models.TransactionID
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.SimpleName
import com.expensetracker.core.support.TransactionDescription
import com.expensetracker.core.support.TransactionResponse
import com.expensetracker.data_sqlite.DatabaseSchema
import com.expensetracker.data_sqlite.services.support.IdGenerator

const val TAG = "IncomeService =>log"
class IncomeService(
    private val db: SQLiteDatabase,
    private val idGenerator: IdGenerator,
    private val schema: DatabaseSchema.IncomeTable,
    private val incomeCategoryActions: IncomeCategoryActions,
    private val bankAccountActions: BankAccountActions,
    private val cashAccountActions: CashAccountActions,
    private val creditCardActions: CreditCardActions,
    private val debitCardActions: DebitCardActions
): IncomeActions{
    override fun generateId(): Int = idGenerator.newId

    override fun hasTransaction(id: TransactionID): Boolean {
        return try {
            val cursor = db.query(
                schema.TABLE_NAME,
                arrayOf(schema.ID),
                "${schema.ID} = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )
            val exists = cursor.moveToFirst()
            cursor.close()
            exists
        } catch (e: Exception) {
            false
        }
    }

    override fun getTransaction(id: TransactionID): Income? {
        return try {
            val cursor = db.query(
                schema.TABLE_NAME,
                schema.columns,
                "${schema.ID} = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            val income = if (cursor.moveToFirst()) {
                val accountID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ACCOUNT_ID))
                val account =
                    bankAccountActions.getAccount(accountID) ?:
                    cashAccountActions.getAccount(accountID) ?:
                    creditCardActions.getAccount(accountID) ?:
                    debitCardActions.getAccount(accountID) ?:
                    return null
                Income(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ID)),
                    date = cursor.getLong(cursor.getColumnIndexOrThrow(schema.DATE)),
                    amount = Amount(cursor.getDouble(cursor.getColumnIndexOrThrow(schema.AMOUNT)).toString()),
                    note = SimpleName(cursor.getString(cursor.getColumnIndexOrThrow(schema.NOTE))),
                    description = TransactionDescription(cursor.getString(cursor.getColumnIndexOrThrow(schema.DESCRIPTION))),
                    category = incomeCategoryActions.getCategory(cursor.getInt(cursor.getColumnIndexOrThrow(schema.CATEGORY_ID))) ?: return null,
                    account = account
                )
            } else {
                null
            }
            cursor.close()
            income
        } catch (e: Exception) {
            null
        }
    }

    override fun getSomeTransactions(
        offset: Long,
        limit: Long,
        predicate: (Income) -> Boolean,
    ): List<Income> {
        return try {
            val transactions = mutableListOf<Income>()
            val cursor = db.query(
                schema.TABLE_NAME,
                schema.columns,
                null,
                null,
                null,
                null,
                null
            )

            var limit1 = 0

            if (cursor.move(offset.toInt()+1)) {
                do {
                    val accountID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ACCOUNT_ID))
                    val account =
                        bankAccountActions.getAccount(accountID) ?:
                        cashAccountActions.getAccount(accountID) ?:
                        creditCardActions.getAccount(accountID) ?:
                        debitCardActions.getAccount(accountID) ?:
                        continue
                    val income = Income(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ID)),
                        date = cursor.getLong(cursor.getColumnIndexOrThrow(schema.DATE)),
                        amount = Amount(cursor.getDouble(cursor.getColumnIndexOrThrow(schema.AMOUNT)).toString()),
                        note = SimpleName(cursor.getString(cursor.getColumnIndexOrThrow(schema.NOTE))),
                        description = TransactionDescription(cursor.getString(cursor.getColumnIndexOrThrow(schema.DESCRIPTION))),
                        category = incomeCategoryActions.getCategory(cursor.getInt(cursor.getColumnIndexOrThrow(schema.CATEGORY_ID))) ?: continue,
                        account = account
                    )
                    transactions.add(income)
                    limit1++
                } while (cursor.moveToNext() && limit1 < limit)
            }
            cursor.close()
            transactions
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getSomeTransactionsBetween(
        from: TransactionDate,
        to: TransactionDate,
        offset: Long,
        limit: Long,
        predicate: (Income) -> Boolean,
    ): List<Income> {
        return try {
            val transactions = mutableListOf<Income>()
            val cursor = db.query(
                schema.TABLE_NAME,
                schema.columns,
                null,
                null,
                null,
                null,
                null
            )

            var limit1 = 0

            if (
cursor.move(offset.toInt()+1)) {
                do {
                    val accountID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ACCOUNT_ID))
                    val account =
                        bankAccountActions.getAccount(accountID) ?:
                        cashAccountActions.getAccount(accountID) ?:
                        creditCardActions.getAccount(accountID) ?:
                        debitCardActions.getAccount(accountID) ?:
                        continue
                    val income = Income(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ID)),
                        date = cursor.getLong(cursor.getColumnIndexOrThrow(schema.DATE)),
                        amount = Amount(cursor.getDouble(cursor.getColumnIndexOrThrow(schema.AMOUNT)).toString()),
                        note = SimpleName(cursor.getString(cursor.getColumnIndexOrThrow(schema.NOTE))),
                        description = TransactionDescription(cursor.getString(cursor.getColumnIndexOrThrow(schema.DESCRIPTION))),
                        category = incomeCategoryActions.getCategory(cursor.getInt(cursor.getColumnIndexOrThrow(schema.CATEGORY_ID))) ?: continue,
                        account = account
                    )
                    if(income.date in to..from) {
                        transactions.add(income)
                        limit1++
                    }
                } while (cursor.moveToNext() && limit1 < limit)
            }
            cursor.close()
            transactions
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getTransactions(from: TransactionDate, to: TransactionDate): List<Income> {
        return try {
            val transactions = mutableListOf<Income>()
            val cursor = db.query(
                schema.TABLE_NAME,
                schema.columns,
                null,
                null,
                null,
                null,
                null
            )


            if (cursor.moveToFirst()) {
                do {
                    val accountID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ACCOUNT_ID))
                    val account =
                        bankAccountActions.getAccount(accountID) ?:
                        cashAccountActions.getAccount(accountID) ?:
                        creditCardActions.getAccount(accountID) ?:
                        debitCardActions.getAccount(accountID) ?:
                        continue
                    val income = Income(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ID)),
                        date = cursor.getLong(cursor.getColumnIndexOrThrow(schema.DATE)),
                        amount = Amount(cursor.getDouble(cursor.getColumnIndexOrThrow(schema.AMOUNT)).toString()),
                        note = SimpleName(cursor.getString(cursor.getColumnIndexOrThrow(schema.NOTE))),
                        description = TransactionDescription(cursor.getString(cursor.getColumnIndexOrThrow(schema.DESCRIPTION))),
                        category = incomeCategoryActions.getCategory(cursor.getInt(cursor.getColumnIndexOrThrow(schema.CATEGORY_ID))) ?: continue,
                        account = account
                    )

                    if(income.date in to..from)
                        transactions.add(income)
                } while (cursor.moveToNext())
            }
            cursor.close()
            transactions
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getTotalOfTransactions(from: TransactionDate, to: TransactionDate): Amount {
        return try {
            val transactions = getTransactions(from, to)
            var amount = Amount("0.0")
            transactions.forEach {
                amount += it.amount
            }
            amount
        } catch (e: Exception) {
            Amount.DEFAULT
        }
    }

    override fun getTransactionsCount(from: TransactionDate, to: TransactionDate): Int {
        return getTransactions(from, to).count()
    }

    override fun addTransaction(transaction: Income): TransactionResponse {
        return try {
            db.beginTransaction()
            if (hasTransaction(transaction.id)) {
                db.setTransactionSuccessful()
                return TransactionResponse.TRANSACTION_ALREADY_EXIST
            }

            val contentValues = ContentValues().apply {
                put(schema.ID, transaction.id)
                put(schema.DATE, transaction.date)
                put(schema.AMOUNT, transaction.amount.toString().toDouble())
                put(schema.NOTE, transaction.note.toString())
                put(schema.DESCRIPTION, transaction.description.toString())
                put(schema.CATEGORY_ID, transaction.category.id)
                put(schema.ACCOUNT_ID, transaction.account.id)
            }

            val result = db.insert(schema.TABLE_NAME, null, contentValues)
            if (result != -1L) {
                db.setTransactionSuccessful()
                TransactionResponse.TRANSACTION_CREATED
            } else {
                TransactionResponse.TRANSACTION_NOT_EXIST
            }
        } catch (e: Exception) {
            TransactionResponse.TRANSACTION_NOT_EXIST
        } finally {
            db.endTransaction()
        }
    }

    override fun updateTransaction(transaction: Income): TransactionResponse {
        return try {
            db.beginTransaction()
            if (!hasTransaction(transaction.id)) {
                db.setTransactionSuccessful()
                return TransactionResponse.TRANSACTION_NOT_EXIST
            }

            val contentValues = ContentValues().apply {
                put(schema.ID, transaction.id)
                put(schema.DATE, transaction.date)
                put(schema.AMOUNT, transaction.amount.toString().toDouble())
                put(schema.NOTE, transaction.note.toString())
                put(schema.DESCRIPTION, transaction.description.toString())
                put(schema.CATEGORY_ID, transaction.category.id)
                put(schema.ACCOUNT_ID, transaction.account.id)
            }

            val result = db.update(
                schema.TABLE_NAME,
                contentValues,
                "${schema.ID} = ?",
                arrayOf(transaction.id.toString())
            )
            if (result > 0) {
                db.setTransactionSuccessful()
                TransactionResponse.TRANSACTION_UPDATED
            } else {
                TransactionResponse.TRANSACTION_ALREADY_EXIST
            }
        } catch (e: Exception) {
            TransactionResponse.TRANSACTION_ALREADY_EXIST
        } finally {
            db.endTransaction()
        }
    }

    override fun deleteTransaction(id: TransactionID): TransactionResponse {
        return try {
            db.beginTransaction()
            val result = db.delete(
                schema.TABLE_NAME,
                "${schema.ID} = ?",
                arrayOf(id.toString())
            )
            if (result > 0) {
                db.setTransactionSuccessful()
                TransactionResponse.TRANSACTION_DELETED
            } else {
                TransactionResponse.TRANSACTION_NOT_EXIST
            }
        } catch (e: Exception) {
            TransactionResponse.TRANSACTION_NOT_EXIST
        } finally {
            db.endTransaction()
        }
    }

}