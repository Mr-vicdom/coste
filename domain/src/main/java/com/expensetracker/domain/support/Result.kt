package com.expensetracker.domain.support

sealed class Result(open val data: String) {
    class Success(override val data: String): Result(data)
    class Failure(override val data: String): Result(data)

    override fun toString(): String {
        return "${this.javaClass.simpleName} $data"
    }
}