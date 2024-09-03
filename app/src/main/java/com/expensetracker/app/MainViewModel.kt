package com.expensetracker.app

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val _isBackPressed: MutableLiveData<Boolean> = MutableLiveData()

    var selectedFrag: Fragment? = null

    val isBackPressed: LiveData<Boolean>
        get() = _isBackPressed

    fun postBackPressed() {
        _isBackPressed.postValue(true)
    }
}