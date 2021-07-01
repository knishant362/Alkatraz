package com.trendster.harpic.ui.user.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.trendster.harpic.R
import com.trendster.harpic.databinding.FragmentStartBinding
import com.trendster.harpic.ui.MainActivity
import com.trendster.harpic.util.NetworkResult
import com.trendster.harpic.viewmodels.UserViewModel

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var RC_SIGN_IN = 1
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btnEmailLogin.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }
        binding.txtTextSignup.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_signupFragment)
        }
        binding.btnGoogleSignin.setOnClickListener {
            signIn()
        }

        userViewModel.userRegistrationCheckResponse.observe(
            viewLifecycleOwner,
            { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        showToast("User already exist, loading")
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    is NetworkResult.Error -> {
                        showToast("No User found, creating account.")
                        if (response.message.toString() == "User not Registered") {
                            if (auth.currentUser?.uid != null || auth.currentUser?.displayName == null) {
                                userViewModel.createUser(auth.currentUser?.uid!!, auth.currentUser?.displayName!!)
                            }
                        }
                    }
                    is NetworkResult.Loading -> {
                        showToast("Loading")
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
                        showToast(response.data?.message!!)
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    is NetworkResult.Error -> {
                        showToast("Creation Error")
                    }
                    is NetworkResult.Loading -> {
                        showToast("Loading")
                    }
                }
            }
        )

        return binding.root
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        showToast(user?.uid.toString())
        if (user != null) {
            userViewModel.checkRegistration(user.uid)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
            }
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
