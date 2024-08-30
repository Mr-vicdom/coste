package com.expensetracker.data.services.support

class IdGenerator {
    var newId : Int = 0
        get() {
            return field++
        }
        private set
}