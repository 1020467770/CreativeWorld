package cn.sqh.creativeworld.login.ui.login

import androidx.lifecycle.Observer
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.sqh.creativeworld.MainActivity
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.data.Resource
import cn.sqh.creativeworld.databinding.FragmentLoginBinding
import cn.sqh.creativeworld.login.data.model.LoggingUser
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


class LoginFragment : Fragment() {

    private lateinit var mDataBinding: FragmentLoginBinding

    private val args: LoginFragmentArgs by navArgs()

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }

    private val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // ignore
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // ignore
        }

        override fun afterTextChanged(s: Editable) {
            loginViewModel.loginDataChanged(
                mDataBinding.email.text.toString(),
                mDataBinding.password.text.toString()
            )
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
        mDataBinding = FragmentLoginBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = loginViewModel
            loggingUser = args.loggingUser ?: LoggingUser()
            login.setOnClickListener {
                mDataBinding.loading.visibility = View.VISIBLE
                loginViewModel.login(mDataBinding.loggingUser)
            }
            email.addTextChangedListener(afterTextChangedListener)
            password.addTextChangedListener(afterTextChangedListener)
            password.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(mDataBinding.loggingUser)
                }
                false
            }
            tvRegisterNavigation.setOnClickListener {
                val directions = LoginFragmentDirections.actionLoginToRegister()
                findNavController().navigate(directions)
            }
        }
        return mDataBinding.root
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel.loggedInUser.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Resource.Status.SUCCESS -> {
                    mDataBinding.loading.visibility = View.GONE
                    MainActivity.start(requireContext())
                    requireActivity().finish()
                }
                Resource.Status.ERROR -> {
                    mDataBinding.loading.visibility = View.GONE
                    mDataBinding.login.isEnabled =
                        loginViewModel.loginFormState.value?.isDataValid ?: false
                    showLoginFailed(R.string.login_failed)

                }
                Resource.Status.LOADING -> {
                    mDataBinding.loading.visibility = View.VISIBLE
                    mDataBinding.login.isEnabled = false
                }
            }
            LogUtils.d(result)
        }

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginFormState.usernameError?.let {
                    mDataBinding.email.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    mDataBinding.password.error = getString(it)
                }
            })

    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}