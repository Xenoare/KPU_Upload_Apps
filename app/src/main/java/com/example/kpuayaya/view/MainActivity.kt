package com.example.kpuayaya.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kpuayaya.R
import com.example.kpuayaya.databinding.ActivityMainBinding
import com.example.kpuayaya.utils.Toaster

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        when (hour) {
            in 6..12 -> {
                binding.tvGreetings.text = "Selamat Pagi";
                binding.ivWeather.setImageResource(R.drawable.ic_morning)
            }
            in 13..15 -> {
                binding.tvGreetings.text = "Selamat Siang";
                binding.ivWeather.setImageResource(R.drawable.ic_evening)
            }
            in 16..17 -> {
                binding.tvGreetings.text = "Selamat Sore";
                binding.ivWeather.setImageResource(R.drawable.ic_afternoon)
            }
            else -> {
                binding.tvGreetings.text = "Selamat Malam";
                binding.ivWeather.setImageResource(R.drawable.ic_night)
            }
        }

        binding.content.cvCheckDpt.setOnClickListener(this)
        binding.content.cvSosialisasi.setOnClickListener(this)
        binding.content.cvInputData.setOnClickListener(this)


    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.cvCheckDpt -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        val intent = Intent(ACTION_VIEW, Uri.parse("https://cekdptonline.kpu.go.id/")).apply {
                            // The URL should either launch directly in a non-browser app
                            // (if it’s the default), or in the disambiguation dialog
                            addCategory(CATEGORY_BROWSABLE)
                            flags = FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toaster.show(
                            this@MainActivity,
                            "(API 11) No browser available to open the URL",
                        )
                    }

                } else {
                    val intent = Intent(ACTION_VIEW, Uri.parse("https://cekdptonline.kpu.go.id/"))

                    if (intent.resolveActivity(this.packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toaster.show(
                            this,
                            "No browser available to open the URL",
                        )
                    }

                }

            }
            R.id.cvSosialisasi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        val intent = Intent(ACTION_VIEW, Uri.parse("https://sosialisasi-kpu.vercel.app/")).apply {
                            // The URL should either launch directly in a non-browser app
                            // (if it’s the default), or in the disambiguation dialog
                            addCategory(CATEGORY_BROWSABLE)
                            flags = FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toaster.show(
                            this@MainActivity,
                            "(API 11) No browser available to open the URL",
                        )
                    }

                } else {
                    val intent = Intent(ACTION_VIEW, Uri.parse("https://sosialisasi-kpu.vercel.app/"))

                    if (intent.resolveActivity(this.packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toaster.show(
                            this,
                            "No browser available to open the URL",
                        )
                    }

                }

            }
            R.id.cvInputData -> {
                val intent = Intent(this, UploadActivity::class.java)
                startActivity(intent)
            }
        }
    }
}