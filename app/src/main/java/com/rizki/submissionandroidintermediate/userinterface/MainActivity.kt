package com.rizki.submissionandroidintermediate.userinterface

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rizki.submissionandroidintermediate.R
import com.rizki.submissionandroidintermediate.UserPreferences
import com.rizki.submissionandroidintermediate.databinding.ActivityMainBinding
import com.rizki.submissionandroidintermediate.dataclass.LoginDataAccount
import com.rizki.submissionandroidintermediate.viewmodel.DataStoreViewModel
import com.rizki.submissionandroidintermediate.viewmodel.MainViewModel
import com.rizki.submissionandroidintermediate.viewmodel.MainViewModelFactory
import com.rizki.submissionandroidintermediate.viewmodel.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val loginViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ifClicked()
        playAnimation()

        val preferences = UserPreferences.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]

        dataStoreViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this@MainActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        loginViewModel.message.observe(this) { message ->
            responseLogin(
                message,
                dataStoreViewModel
            )
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun responseLogin(
        message: String,
        dataStoreViewModel: DataStoreViewModel
    ) {
        if (message.contains("Hello")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val user = loginViewModel.userlogin.value
            dataStoreViewModel.saveLoginSession(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.iconLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvLoginDesc =
            ObjectAnimator.ofFloat(binding.tvLoginDescription, View.ALPHA, 1f).setDuration(300)
        val tvLogin = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(300)
        val evEmail = ObjectAnimator.ofFloat(binding.CVEmail, View.ALPHA, 1f).setDuration(300)
        val evPassword =
            ObjectAnimator.ofFloat(binding.PasswordLogin, View.ALPHA, 1f).setDuration(300)
        val seePassword =
            ObjectAnimator.ofFloat(binding.seePassword, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)
        val tvRegistDesc =
            ObjectAnimator.ofFloat(binding.tvRegistDescription, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                tvLoginDesc,
                tvLogin,
                evEmail,
                evPassword,
                seePassword,
                btnLogin,
                btnRegister,
                tvRegistDesc
            )
            start()
        }
    }

    private fun ifClicked() {
        binding.btnLogin.setOnClickListener {
            binding.CVEmail.clearFocus()
            binding.PasswordLogin.clearFocus()

            if (isDataValid()) {
                val requestLogin = LoginDataAccount(
                    binding.CVEmail.text.toString().trim(),
                    binding.PasswordLogin.text.toString().trim()
                )
                loginViewModel.login(requestLogin)
            } else {
                if (!binding.CVEmail.isEmailValid) binding.CVEmail.error =
                    getString(R.string.emailNone)
                if (!binding.PasswordLogin.isPasswordValid) binding.PasswordLogin.error =
                    getString(R.string.passwordNone)

                Toast.makeText(this, R.string.invalidLogin, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        binding.seePassword.setOnCheckedChangeListener { _, isChecked ->
            binding.PasswordLogin.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }

            binding.PasswordLogin.text?.let { binding.PasswordLogin.setSelection(it.length) }
        }
    }

    private fun isDataValid(): Boolean {
        return binding.CVEmail.isEmailValid && binding.PasswordLogin.isPasswordValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
