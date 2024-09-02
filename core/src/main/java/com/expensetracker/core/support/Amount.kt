package com.expensetracker.core.support

@JvmInline
value class Amount(val value: String) {

    companion object {
        val DEFAULT = Amount("0.0")
    }

    override fun toString(): String {
        return value
    }
    init {
        if(!value.matches(Regex(Pattern.FLOAT_REGEX_PATTERN))){
            throw CustomException.InvalidAmountException
        }
    }

    operator fun minus(amount: Amount): Amount {
        val currentValue: Double = this.value.toDouble()
        val operandValue: Double = amount.value.toDouble()
        val difference = currentValue - operandValue
        return Amount(Pattern.FLOAT_TWO_DECIMALS.format(difference))
    }

    operator fun plus(amount: Amount): Amount {
        val currentValue: Double = this.value.toDouble()
        val operandValue: Double = amount.value.toDouble()
        val sum = currentValue + operandValue
        return Amount(Pattern.FLOAT_TWO_DECIMALS.format(sum))
    }
}