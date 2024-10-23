package com.example.kpuayaya.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kpuayaya.R
import com.example.kpuayaya.adapter.RecentAdapter
import com.example.kpuayaya.databinding.FragmentProgressBinding
import com.example.kpuayaya.model.PostModel
import com.example.kpuayaya.model.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.play.integrity.internal.i
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ProgressFragment : Fragment(), RecentAdapter.OnItemClickListener {

    private lateinit var binding: FragmentProgressBinding
    private val user: FirebaseUser? =Firebase.auth.currentUser
    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter : RecentAdapter? = null
    private val userRef = db.collection("users").document(user?.uid!!)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProgressBinding.inflate(inflater, container, false)

        userRef.get().addOnSuccessListener {item ->
            with(binding) {
                val profile = item.toObject<User>()
                layoutHeader.tvName.text = profile?.username
                layoutHeader.tvEmail.text = profile?.email
            }
        }
        setupMenu()
        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        val query : Query = userRef.collection("recent")

        val options = FirestoreRecyclerOptions.Builder<PostModel>()
            .setQuery(query, PostModel::class.java)
            .build()

        adapter = RecentAdapter(options, this)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    private fun setupMenu() {
        binding.topAppBar.setOnMenuItemClickListener {menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    Firebase.auth.signOut()
                    findNavController().navigate(R.id.loginFragment)
                    true
                }
                else -> {true}

            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    override fun onItemClick(documentId: String) {
        val action = ProgressFragmentDirections.actionProgressFragmentToUpdateFragment(documentId)
        findNavController().navigate(action)
    }
}