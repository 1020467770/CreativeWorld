package cn.sqh.creativeworld.core.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import cn.sqh.creativeworld.R

object AccountStore {

    private val allUserAccounts = mutableListOf(
        Account(
            1L,
            0L,
            "zhangsan",
            "zhangsan@qq.com",
            R.drawable.avatar_10,
            true
        ),
        Account(
            2L,
            0L,
            "lisi",
            "lisi@qq.com",
            R.drawable.avatar_2
        ),
        Account(
            3L,
            0L,
            "wangwu",
            "wangwu@qq.com",
            R.drawable.avatar_9
        )
    )


    private val _userAccounts: MutableLiveData<List<Account>> = MutableLiveData()
    private val _currentAccount :MutableLiveData<Account> = MutableLiveData()


    val userAccounts: LiveData<List<Account>>
        get() = _userAccounts

    init {
        postUpdateUserAccountsList()
    }

    fun getDefaultUserAccount() = allUserAccounts.first()

    fun getAllUserAccounts() = allUserAccounts

    fun setCurrentUserAccount(accountId: Long): Boolean {
        var updated = false
        allUserAccounts.forEachIndexed { index, account ->
            val shouldCheck = account.id == accountId
            if (account.isCurrentAccount != shouldCheck) {
                allUserAccounts[index] = account.copy(isCurrentAccount = shouldCheck)
                updated = true
            }
        }
        if (updated) postUpdateUserAccountsList()
        return updated
    }

    private fun postUpdateUserAccountsList() {
        val newList = allUserAccounts.toList()
        _userAccounts.value = newList
    }
}