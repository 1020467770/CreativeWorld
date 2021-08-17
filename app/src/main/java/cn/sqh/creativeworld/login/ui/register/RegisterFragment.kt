package cn.sqh.creativeworld.login.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.data.Resource
import cn.sqh.creativeworld.databinding.FragmentLoginBinding
import cn.sqh.creativeworld.databinding.FragmentRegisterBinding
import cn.sqh.creativeworld.login.data.model.LoggingUser
import cn.sqh.creativeworld.login.data.model.RegisteringUser
import cn.sqh.creativeworld.login.ui.login.LoginViewModel
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.transition.MaterialSharedAxis

class RegisterFragment : Fragment() {

    private lateinit var mDataBinding: FragmentRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels()

    private val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // ignore
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // ignore
        }

        override fun afterTextChanged(s: Editable) {
            registerViewModel.registerDataChanged(mDataBinding.registerUser!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_small).toLong()
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_small).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDataBinding = FragmentRegisterBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = registerViewModel
            registerUser = RegisteringUser()
            toolbar.setNavigationOnClickListener {
                val directions =
                    RegisterFragmentDirections.actionRegisterToLogin()
                findNavController().navigate(directions)
            }
            email.addTextChangedListener(afterTextChangedListener)
            username.addTextChangedListener(afterTextChangedListener)
            password.addTextChangedListener(afterTextChangedListener)
            btnRegister.setOnClickListener {
                mDataBinding.loading.visibility = View.VISIBLE
                registerViewModel.register(mDataBinding.registerUser!!)
            }
        }
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    val directions =
                        RegisterFragmentDirections.actionRegisterToLogin()
                    findNavController().navigate(directions)
                }
            }
        )

        registerViewModel.registerFormState.observe(viewLifecycleOwner,
            Observer { registerFormState ->
                if (registerFormState == null) {
                    return@Observer
                }
                registerFormState.usernameError?.let {
                    mDataBinding.username.error = getString(it)
                }
                registerFormState.emailError?.let {
                    mDataBinding.email.error = getString(it)
                }
            })

        registerViewModel.registerUser.observe(viewLifecycleOwner) { registeResult ->
            when (registeResult.status) {
                Resource.Status.SUCCESS -> {
                    mDataBinding.loading.visibility = View.GONE
                    val prepared_loggingUser =
                        LoggingUser(
                            mDataBinding.email.text?.trim().toString(),
                            mDataBinding.password.text?.trim().toString()
                        )
                    val directions =
                        RegisterFragmentDirections.actionRegisterToLogin(prepared_loggingUser)
                    ToastUtils.showShort(R.string.register_success)
                    findNavController().navigate(directions)
                }
                Resource.Status.ERROR -> {
                    mDataBinding.loading.visibility = View.GONE
                    showLoginFailed(R.string.register_failed)
                }
                Resource.Status.LOADING -> {
                    mDataBinding.loading.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}