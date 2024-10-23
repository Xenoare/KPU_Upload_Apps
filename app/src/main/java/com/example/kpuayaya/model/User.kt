package com.example.kpuayaya.model

data class User(
    val username: String,
    val email: String,
    val uid: String
) {
    constructor() : this("", "", "")
}
