package cn.sqh.creativeworld.login

import android.os.Bundle
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.BaseActivity
import cn.sqh.creativeworld.databinding.ActivityLoginBinding
import cn.sqh.creativeworld.utils.contentView

class LoginActivity : BaseActivity() {

    private val mDataBinding: ActivityLoginBinding by contentView(R.layout.activity_login)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding.run {

        }
    }
}