package com.example.kpuayaya.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.kpuayaya.MainActivity
import com.example.kpuayaya.R
import com.example.kpuayaya.databinding.FragmentUploadBinding
import com.example.kpuayaya.model.CitiesModel
import com.example.kpuayaya.model.PostModel
import com.example.kpuayaya.utils.Toaster
import com.example.kpuayaya.utils.getPath
import com.example.kpuayaya.utils.locationConverter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class UploadFragment : Fragment() {
    private lateinit var binding: FragmentUploadBinding
    private lateinit var inputAlamat: Array<String>
    private lateinit var inputAlamatSelected: String
    private lateinit var currentUri: Uri
    private lateinit var nama: String
    private lateinit var alamat: String
    private lateinit var desc: String
    private lateinit var uri: String
    private lateinit var currentDate: String
    private var total = 0
    private var storageRef = Firebase.storage.reference
    private var tempTotal = 0
    private lateinit var downloadUrl: Uri
    private var db = Firebase.firestore
    private var user: FirebaseUser? = Firebase.auth.currentUser
    private var userRef: DocumentReference = db.collection("users").document(user?.uid!!)

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                currentUri = uri
                val fileName = getPath(requireContext(), uri)
                binding.tvFilename.text = fileName
                binding.ivImage.setImageURI(uri)
            } else {
                Toaster.show(requireContext(), "Failed")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadBinding.inflate(inflater, container, false)

        setInitLayout()

        return binding.root
    }

    private fun setInitLayout() {
        inputAlamat = resources.getStringArray(R.array.location)

        val arrayLocation = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, inputAlamat)
        arrayLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spLocation.adapter = arrayLocation

        binding.spLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                inputAlamatSelected =
                    locationConverter(parent.getItemAtPosition(position).toString())
                binding.spLocation.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.ivImage.setOnClickListener {
            galleryLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.btnUpload.setOnClickListener {
            nama = binding.inputNama.text.toString()
            alamat = inputAlamatSelected
            total = binding.inputTotal.text.toString().toInt()
            desc = binding.inputDesc.text.toString()
            uri = currentUri.toString()
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Indonesia/Jakarta"))
            val dateFormat = SimpleDateFormat("EEE MMM dd yyyy")
            currentDate = dateFormat.format(calendar.time)

            if (nama.isEmpty() or alamat.isEmpty() or (total == 0) or desc.isEmpty() or uri.isEmpty()) {
                Toaster.show(
                    requireContext(), "Data tidak boleh ada yang kosong!",
                )
            } else {
                val storageRef = storageRef.child("images/${alamat}")
                storageRef.putFile(currentUri).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        downloadUrl = task.result
                        setUpload()
                    } else {
                        Toaster.show(requireContext(), "Gagal Upload")
                    }
                }

                Toaster.show(requireContext(), "Successfully uploaded image")
            }
        }
    }


    private fun setUpload() {

        val data = PostModel(
            description = desc,
            file = downloadUrl.toString(),
            key = alamat,
            name = nama,
            timestamp = currentDate,
            total = total
        )

        userRef.collection("recent").add(data)

        val citiesRef = db.collection("locations").document(alamat)
        citiesRef.get()
        citiesRef.update("current_coklit", tempTotal + data.total)

        val postRef = db.collection("locations").document(data.key).collection("posts")
        postRef.add(data)

        val recentRef = db.collection("recent")
        recentRef.add(data)

        Toaster.show(requireContext(), "Success Uploading")
        findNavController().navigate(R.id.homeFragment)
    }

}