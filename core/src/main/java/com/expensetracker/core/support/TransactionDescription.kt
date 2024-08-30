package com.expensetracker.core.support

@JvmInline
value class TransactionDescription(val value: String) {
    init {
        if(value.length > 1000){
            throw CustomException.InvalidTransactionDescriptionException
        }
    }

    override fun toString(): String {
        return value
    }
}