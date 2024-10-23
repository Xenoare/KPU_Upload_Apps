package com.example.kpuayaya.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgument
import coil.load
import com.example.kpuayaya.R
import com.example.kpuayaya.databinding.FragmentUpdateBinding
import com.example.kpuayaya.model.PostModel
import com.example.kpuayaya.utils.Toaster
import com.example.kpuayaya.utils.getPath
import com.example.kpuayaya.utils.locationConverter
import com.example.kpuayaya.utils.mapLocationToNumber
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
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

class UpdateFragment : Fragment() {

    private lateinit var binding : FragmentUpdateBinding
    private val args: UpdateFragmentArgs by navArgs()
    private lateinit var documentId : String
    private lateinit var currentUri: Uri
    private lateinit var nama: String
    private lateinit var alamat: String
    private lateinit var desc: String
    private lateinit var uri: String
    private lateinit var currentDate: String
    private lateinit var downloadUrl: Uri
    private var total = 0
    private var storageRef = Firebase.storage.reference
    private var post : PostModel? = null
    private lateinit var inputAlamat: Array<String>
    private var user: FirebaseUser? = Firebase.auth.currentUser
    private val db = Firebase.firestore
    private lateinit var inputAlamatSelected: String
    private val userRef = db.collection("users").document(user?.uid!!)

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateBinding.inflate(inflater, container, false)

        setInitLayout()

        documentId = args.documentId

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete the Report")
                        .setMessage("Apakah kamu yakin ingin menghapus Report ini")
                        .setPositiveButton("Iya") { dialog, _ ->
                            val documentRef = userRef.collection("recent").document(documentId)
                            documentRef.delete()
                            findNavController().navigate(R.id.progressFragment)
                        }
                        .setNegativeButton("Tidak") {dialog, _ ->
                            dialog.dismiss()
                        }

                    true
                }
                else -> {true}

            }
        }
        val recentRef = userRef.collection("recent").document(documentId)
        recentRef.get().addOnSuccessListener {
            post = it.toObject<PostModel>()

            binding.inputNama.setText(post?.name)
            binding.inputTotal.setText(post?.total!!.toString())
            binding.inputDesc.setText(post?.description)
            binding.spLocation.setSelection(mapLocationToNumber(post?.key))
            binding.ivImage.load(post?.file)
            currentUri = Uri.parse(post?.file)
        }

        binding.btnUpload.setOnClickListener {
            nama = binding.inputNama.text.toString()
            alamat = inputAlamatSelected
            total = binding.inputTotal.text.toString().toInt()
            desc = binding.inputDesc.text.toString()
            uri =  currentUri.toString()
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


        return binding.root

    }


    private fun setUpload() = CoroutineScope(Dispatchers.IO).launch {
        val postMap = mapOf(
            "description" to desc,
            "file" to downloadUrl.toString(),
            "key" to alamat,
            "name" to nama,
            "timestamp" to currentDate,
            "total" to total
        )

        val documentRef = userRef.collection("recent").document(documentId)

        documentRef.update(postMap)
            .addOnSuccessListener {
                Toaster.show(requireContext(), "Success Update Content")
                findNavController().navigate(R.id.progressFragment)
            }

    }

    private fun setInitLayout() {
        inputAlamat = resources.getStringArray(R.array.location)

        val arrayLocation =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, inputAlamat)
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
    }

}