package com.expensetracker.data_sqlite.services.accounts

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.expensetracker.core.actions.BankAccountActions
import com.expensetracker.core.actions.DebitCardActions
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.DebitCard
import com.expensetracker.core.support.AccountResponse
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.SimpleName
import com.expensetracker.data_sqlite.DatabaseSchema
import com.expensetracker.data_sqlite.services.support.IdGenerator

class DebitCardService(
    private val db: SQLiteDatabase,
    private val idGenerator: IdGenerator,
    private val schema: DatabaseSchema.DebitCardTable,
    private val bankAccountActions: BankAccountActions
): DebitCardActions {

    override fun generateId(): AccountID = idGenerator.newId

    override fun addAccount(account: DebitCard): AccountResponse {
        return try {
            db.beginTransaction()
            if (hasAccount(account.id)) {
                db.setTransactionSuccessful()
                return AccountResponse.ACCOUNT_ALREADY_EXIST
            }

            if(!bankAccountActions.hasAccount(account.bankAccount.id)){
                db.setTransactionSuccessful()
                return AccountResponse.ACCOUNT_NOT_EXIST
            }

            val contentValues = ContentValues().apply {
                put(schema.ID, account.id)
                put(schema.BANK_ACCOUNT_ID, account.bankAccount.id)
                put(schema.NAME, account.name.toString())
                put(schema.BALANCE, account.balance.toString().toDouble())
                put(schema.DEBIT_LIMIT, account.limit.toString().toDouble())
            }

            val result = db.insert(schema.TABLE_NAME, null, contentValues)
            if (result != -1L) {
                db.setTransactionSuccessful()
                AccountResponse.ACCOUNT_CREATED
            } else {
                AccountResponse.ACCOUNT_NOT_EXIST
            }
        } catch (e: Exception) {
            AccountResponse.ACCOUNT_NOT_EXIST
        } finally {
            db.endTransaction()
        }
    }

    override fun hasAccount(accountID: AccountID): Boolean {
        return try {
            val cursor = db.query(
                schema.TABLE_NAME,
                arrayOf(schema.ID),
                "${schema.ID} = ?",
                arrayOf(accountID.toString()),
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

    override fun getAccount(accountID: AccountID): DebitCard? {
        return try {
            val cursor = db.query(
                schema.TABLE_NAME,
                schema.columns,
                "${schema.ID} = ?",
                arrayOf(accountID.toString()),
                null,
                null,
                null
            )

            val account = if (cursor.moveToFirst()) {
                val bankAccountID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.BANK_ACCOUNT_ID))
                val bankAccount = bankAccountActions.getAccount(bankAccountID)
                if(bankAccount == null){
                    cursor.close()
                    return null
                }
                DebitCard(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ID)),
                    bankAccount = bankAccount,
                    name = SimpleName(cursor.getString(cursor.getColumnIndexOrThrow(schema.NAME))),
                    balance = Amount(cursor.getDouble(cursor.getColumnIndexOrThrow(schema.BALANCE)).toString()),
                    limit = Amount(cursor.getDouble(cursor.getColumnIndexOrThrow(schema.DEBIT_LIMIT)).toString())
                )
            } else {
                null
            }
            cursor.close()
            account
        } catch (e: Exception) {
            null
        }
    }

    override fun getAllAccounts(): List<DebitCard> {
        return try {
            val accounts = mutableListOf<DebitCard>()
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
                    val bankAccountID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.BANK_ACCOUNT_ID))
                    val bankAccount = bankAccountActions.getAccount(bankAccountID) ?: continue
                    val account = DebitCard(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ID)),
                        bankAccount,
                        name = SimpleName(cursor.getString(cursor.getColumnIndexOrThrow(schema.NAME))),
                        balance = Amount(cursor.getDouble(cursor.getColumnIndexOrThrow(schema.BALANCE)).toString()),
                        limit = Amount(cursor.getDouble(cursor.getColumnIndexOrThrow(schema.DEBIT_LIMIT)).toString())
                    )
                    accounts.add(account)
                } while (cursor.moveToNext())
            }
            cursor.close()
            accounts
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun updateAccount(account: DebitCard): AccountResponse {
        return try {
            db.beginTransaction()
            if (!hasAccount(account.id)) {
                db.setTransactionSuccessful()
                return AccountResponse.ACCOUNT_NOT_EXIST
            }

            val contentValues = ContentValues().apply {
                put(schema.NAME, account.name.toString())
                put(schema.BANK_ACCOUNT_ID, account.bankAccount.id.toString())
                put(schema.BALANCE, account.balance.toString().toDouble())
                put(schema.DEBIT_LIMIT, account.limit.toString().toDouble())
            }

            val result = db.update(
                schema.TABLE_NAME,
                contentValues,
                "${schema.ID} = ?",
                arrayOf(account.id.toString())
            )
            if (result > 0) {
                db.setTransactionSuccessful()
                AccountResponse.ACCOUNT_UPDATED
            } else {
                AccountResponse.ACCOUNT_NOT_EXIST
            }
        } catch (e: Exception) {
            AccountResponse.ACCOUNT_NOT_EXIST
        } finally {
            db.endTransaction()
        }
    }

    override fun deleteAccount(id: AccountID): AccountResponse {
        return try {
            db.beginTransaction()
            val result = db.delete(
                schema.TABLE_NAME,
                "${schema.ID} = ?",
                arrayOf(id.toString())
            )
            if (result > 0) {
                db.setTransactionSuccessful()
                AccountResponse.ACCOUNT_DELETED
            } else {
                AccountResponse.ACCOUNT_NOT_EXIST
            }
        } catch (e: Exception) {
            AccountResponse.ACCOUNT_NOT_EXIST
        } finally {
            db.endTransaction()
        }
    }
}
