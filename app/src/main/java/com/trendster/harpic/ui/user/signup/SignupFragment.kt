package com.trendster.harpic.ui.user.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.trendster.harpic.databinding.FragmentSignupBinding
import com.trendster.harpic.ui.MainActivity
import com.trendster.harpic.util.LoadingDialog
import com.trendster.harpic.util.NetworkResult
import com.trendster.harpic.viewmodels.UserViewModel

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var etName: TextInputLayout
    private lateinit var etEmail: TextInputLayout
    private lateinit var etPassword: TextInputLayout
    private lateinit var btnSignup: Button
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        etName = binding.etName
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        btnSignup = binding.btnSignup

        loadingDialog = LoadingDialog(requireActivity())
        signUp()

        userViewModel.userRegistrationCheckResponse.observe(
            viewLifecycleOwner,
            { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        loadingDialog.dismissDialog()
                        showToast("User already exist")
                    }
                    is NetworkResult.Error -> {
                        userViewModel.createUser(auth.currentUser?.uid!!, etName.editText?.text.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        )

        userViewModel.userCreateResponse.observe(
            viewLifecycleOwner,
            {
                response ->
                when (response) {
                    is NetworkResult.Success -> {
                        showToast("Account created Successfully")
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    is NetworkResult.Error -> {
                        binding.etEmail.editText?.setText(response.message)
                        showToast(response.message!!)
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        )

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun signUp() {
        btnSignup.setOnClickListener {
            when {
                etName.editText?.text?.isEmpty() == true -> {
                    etName.error = "Fill this"
                    return@setOnClickListener
                }
                etEmail.editText?.text?.isEmpty() == true -> {
                    etEmail.error = "Fill this"
                    return@setOnClickListener
                }
                etPassword.editText?.text?.isEmpty() == true -> {
                    etPassword.error = "Fill this"
                    return@setOnClickListener
                }
                else -> {

                    loadingDialog.startLoading()
                    auth.createUserWithEmailAndPassword(etEmail.editText?.text.toString(), etPassword.editText?.text.toString())
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SIGNUP", "createUserWithEmail:success")
                                val user = auth.currentUser
                                updateUI(user)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("SIGNUP", "createUserWithEmail:failure", task.exception)
//                                btnSignup.animError()
                                showToast(task.exception?.message!!)
//                                btnSignup.removeDrawable()
                            }
                        }
                }
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            userViewModel.checkRegistration(user.uid)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
