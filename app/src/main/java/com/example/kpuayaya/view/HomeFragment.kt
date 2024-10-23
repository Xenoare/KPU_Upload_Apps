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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.fragment.findNavController
import com.example.kpuayaya.R
import com.example.kpuayaya.databinding.FragmentHomeBinding
import com.example.kpuayaya.utils.Toaster


class HomeFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

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

        return binding.root
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.cvCheckDpt -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        val intent = Intent(ACTION_VIEW, Uri.parse("https://cekdptonline.kpu.go.id/")).apply {
                            addCategory(CATEGORY_BROWSABLE)
                            flags = FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toaster.show(
                            requireContext(),
                            "(API 11) No browser available to open the URL",
                        )
                    }

                } else {
                    val intent = Intent(ACTION_VIEW, Uri.parse("https://cekdptonline.kpu.go.id/"))

                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toaster.show(
                            requireContext(),
                            "No browser available to open the URL",
                        )
                    }

                }

            }
            R.id.cvSosialisasi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        val intent = Intent(ACTION_VIEW, Uri.parse("https://sosialisasi-kpu.vercel.app/")).apply {
                            addCategory(CATEGORY_BROWSABLE)
                            flags = FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toaster.show(
                            requireContext(),
                            "(API 11) No browser available to open the URL",
                        )
                    }

                } else {
                    val intent = Intent(ACTION_VIEW, Uri.parse("https://sosialisasi-kpu.vercel.app/"))

                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toaster.show(
                            requireContext(),
                            "No browser available to open the URL",
                        )
                    }

                }

            }
            R.id.cvInputData -> {
                findNavController().navigate(R.id.uploadFragment)
            }
        }
    }
}
