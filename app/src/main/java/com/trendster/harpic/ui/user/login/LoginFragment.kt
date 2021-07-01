package com.trendster.harpic.ui.user.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.trendster.harpic.R
import com.trendster.harpic.databinding.FragmentLoginBinding
import com.trendster.harpic.ui.MainActivity
import com.trendster.harpic.util.LoadingDialog

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var etEmail: TextInputLayout
    private lateinit var etPassword: TextInputLayout
    private lateinit var txtSignup: TextView
    private lateinit var btnLogin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        etEmail = binding.etEmail
        etPassword = binding.etPasswordd
        btnLogin = binding.btnLogin
        txtSignup = binding.txtTextSignup

        login()

        loadingDialog = LoadingDialog(requireActivity())

        txtSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun login() {
        btnLogin.setOnClickListener {
            when {

                etEmail.editText?.text?.isEmpty() == true -> {
                    binding.etEmail.error = "Fill this"
                    return@setOnClickListener
                }
                etPassword.editText?.text?.isEmpty() == true -> {
                    etPassword.error = "Fill this"
                    return@setOnClickListener
                }
                else -> {
                    loadingDialog.startLoading()
                    auth.signInWithEmailAndPassword(etEmail.editText?.text.toString(), etPassword.editText?.text.toString())
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginSign", "signInWithEmail:success")
                                val user = auth.currentUser
                                updateUI(user, "success")
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginSign", "signInWithEmail:failure", task.exception)
                                updateUI(null, task.exception?.message!!)
                            }
                        }
                }
            }
        }
    }

    private fun updateUI(user: FirebaseUser?, message: String) {
        if (user != null) {
            loadingDialog.dismissDialog()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        } else {
            loadingDialog.dismissDialog()
            Toast.makeText(
                requireContext(), message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
