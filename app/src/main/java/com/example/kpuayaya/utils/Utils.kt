package com.example.kpuayaya.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast


object Toaster {
    fun show(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

fun locationConverter(location: String) : String {
    return when (location) {
        "Bandar Lampung" -> "bandar-lampung"
        "Lampung Barat" -> "lampung-barat"
        "Lampung Selatan" -> "lampung-selatan"
        "Lampung Timur" -> "lampung-timur"
        "Lampung Utara" -> "lampung-utara"
        "Mesuji" -> "mesuji"
        "Metro" -> "metro"
        "Pesawaran" -> "pesawaran"
        "Pesisir Barat" -> "pesisir-barat"
        "Pringsewu" -> "pringsewu"
        "Tanggamus" -> "tanggamus"
        "Tulang Bawang" -> "tulang-bawang"
        "Tulang Bawang Barat" -> "tulang-bawang-barat"
        "Way Kanan" -> "way-kanan"
        else -> "Not found"
    }
}

fun getPath(context: Context, uri: Uri?): String {
    var result: String? = null
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = context.contentResolver.query(uri!!, proj, null, null, null)
    if (cursor != null) {
        if (cursor.moveToFirst()) {
            val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
            result = cursor.getString(column_index)
        }
        cursor.close()
    }
    if (result == null) {
        result = "Not found"
    }
    return result
}
