package com.expensetracker.core.support

@JvmInline
value class SimpleName(val value: String) {
    init {
        if(value.length > 30){
            throw CustomException.InvalidSimpleNameException
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}
