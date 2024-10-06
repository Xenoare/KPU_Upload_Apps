package com.example.kpuayaya.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.kpuayaya.R
import com.example.kpuayaya.databinding.ActivityUploadBinding
import com.example.kpuayaya.model.CitiesModel
import com.example.kpuayaya.model.PostModel
import com.example.kpuayaya.utils.Toaster
import com.example.kpuayaya.utils.getPath
import com.example.kpuayaya.utils.locationConverter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
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
    private var db = Firebase.firestore
    private var tempTotal = 0
    private lateinit var downloadUrl: Uri

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                currentUri = uri
                val fileName = getPath(this, uri)
                binding.tvFilename.text = fileName
                binding.ivImage.setImageURI(uri)
            } else {
                Toaster.show(this, "Failed")
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitLayout()
    }

    private fun setInitLayout() {
        inputAlamat = resources.getStringArray(R.array.location)

        val arrayLocation = ArrayAdapter(this, android.R.layout.simple_list_item_1, inputAlamat)
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
                    this@UploadActivity, "Data tidak boleh ada yang kosong!",
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
                        Log.d("DownloadUrl",  downloadUrl.toString())
                        setUpload()
                    } else {
                        Toaster.show(this, "Gagal Upload")
                    }
                }

                Toaster.show(this@UploadActivity, "Successfully uploaded image")

            }


        }

    }


    private fun setUpload() = CoroutineScope(Dispatchers.IO).launch {

        val data = PostModel(
            description = desc,
            file = downloadUrl.toString(),
            key = alamat,
            name = nama,
            timestamp = currentDate,
            total = total
        )

        val citiesRef = db.collection("locations").document(alamat)
        citiesRef.get()
            .addOnSuccessListener {
                val city = it.toObject<CitiesModel>()
                tempTotal = city?.current_coklit!!
            }
            .addOnFailureListener { exception ->
                Log.w("Upload", "Error getting documents: ", exception)
            }

        citiesRef.update("current_coklit", tempTotal + data.total)
            .addOnSuccessListener { Log.d("Upload", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("Upload", "Error updating document", e) }

        val postRef = db.collection("locations").document(data.key).collection("posts")
        postRef.add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Upload", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Upload", "Error adding document", e)
            }

        val recentRef = db.collection("recent")
        recentRef.add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Upload", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Upload", "Error adding document", e)
            }

        startActivity(Intent(this@UploadActivity, MainActivity::class.java))


    }

}