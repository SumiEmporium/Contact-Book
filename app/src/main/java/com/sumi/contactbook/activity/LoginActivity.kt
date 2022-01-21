package com.sumi.contactbook.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sumi.contactbook.R
import com.sumi.contactbook.databinding.ActivityLoginBinding
import com.sumi.contactbook.prefs.AppPreference
import com.sumi.contactbook.rest.RestRepository
import com.sumi.contactbook.rest.Status
import com.sumi.contactbook.util.RepositoryProvider.Companion.getRepository
import com.sumi.contactbook.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var repository: RestRepository
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initUi()
    }

    private fun initUi() {

        when (AppPreference.cachedUser) {
            null -> {
                Log.e("user", "null")
            }

            else -> {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )
                )
                finish()
            }
        }

        lifecycleScope.launch {
            val job = launch {
                repository = getRepository()
            }
            job.join()

            setupViewModel()
            setupObserver()

            binding.clSignIn.setOnClickListener {
                val userName = binding.etUserName.text.toString()
                val password = binding.etPass.text.toString()
                if (userName.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Enter user name!", Toast.LENGTH_LONG).show()
                } else if (password.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Enter Password!", Toast.LENGTH_LONG).show()
                }else {
                    viewModel.loginUser("NybSys", "1234")
                }

            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, LoginViewModel.FACTORY(repository))
            .get(LoginViewModel::class.java)
    }

    private fun setupObserver() {
        viewModel.userData.observe(this, {
            when (it.status) {

                Status.LOADING -> {
                    Log.e("Login", "LOADING")
                }

                Status.SUCCESS -> {
                    when (it.data?.responseCode) {
                        200 -> {
                            val result = it.data.responseObj
                            Log.e("Login", "SUCCESS$result")

                            when (result) {
                                null -> {
                                    Toast.makeText(this, "user not found!", Toast.LENGTH_LONG)
                                        .show()
                                }
                                else -> {
                                    Toast.makeText(
                                        this,
                                        "Successfully logged in!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    AppPreference.cachedUser = it.data?.responseObj
                                    Log.e("USERINFO", "ddd${AppPreference.cachedUser}")
                                    startActivity(
                                        Intent(this, MainActivity::class.java)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    )
                                    finishAffinity()
                                }
                            }
                        }

                    }
                }

                Status.ERROR -> {
                    Log.e("Login", "ERROR" + it.message)
                }


            }
        })
    }
}