package com.example.quickseat

//data class UserData(val name: String,val email: String,val reg_no: Int)
data class ApiResponse(
    val data: UserData,
    val error: String?,
    val message: String,
    val status: String
)

data class UserData(
    val name: String,
    val email: String,
    val reg_no: Int
)

