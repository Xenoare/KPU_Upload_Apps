package com.example.kpuayaya.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kpuayaya.R
import com.example.kpuayaya.databinding.FragmentRegisterBinding
import com.example.kpuayaya.utils.Toaster
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val userRef = db.collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener {
            val username = binding.evUsername.text.toString()
            val email = binding.evEmailAddress.text.toString()
            val password = binding.evPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = Firebase.auth.currentUser
                            user?.let {
                                val userData = hashMapOf(
                                    "uId" to user.uid,
                                    "username" to username,
                                    "email" to user.email,
                                )

                                userRef.document(user.uid).set(userData)
                                    .addOnSuccessListener {
                                        Toaster.show(requireContext(), "Success")
                                        findNavController().navigate(R.id.loginFragment)
                                    }
                            }
                            Toast.makeText(requireContext(), "Email sudah terbuat, silahkan login.", Toast.LENGTH_SHORT).show()

                            findNavController().navigate(R.id.loginFragment)
                        } else {
                            Toast.makeText(requireContext(), "Maaf, Email sudah dipakai, silakan coba e-mail lain..", Toast.LENGTH_SHORT).show()
                        }
                    }

            }

        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }


        return binding.root
    }

}