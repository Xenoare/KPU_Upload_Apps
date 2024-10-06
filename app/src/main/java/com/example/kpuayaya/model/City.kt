package com.example.kpuayaya.model

import java.lang.reflect.Constructor

data class CitiesModel(
    val current_coklit: Int,
    val key: String,
    val name: String,
    val total_coklit: Int
) {
    constructor() : this(0, "", "", 0)
}

