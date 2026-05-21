package com.example.data

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("proses_login.php")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse

    @FormUrlEncoded
    @POST("proses_daftar.php")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("handphone") handphone: String,
        @Field("email") email: String,
        @Field("bank") bank: String,
        @Field("no_rekening") noRekening: String
    ): BaseResponse

    @FormUrlEncoded
    @POST("cek_saldo.php")
    suspend fun checkSaldo(
        @Field("username") username: String
    ): SaldoResponse

    @FormUrlEncoded
    @POST("buat_deposit.php")
    suspend fun deposit(
        @Field("username") username: String,
        @Field("amount") amount: Double
    ): BaseResponse

    @FormUrlEncoded
    @POST("buat_withdraw.php")
    suspend fun withdraw(
        @Field("username") username: String,
        @Field("amount") amount: Double
    ): BaseResponse

    @FormUrlEncoded
    @POST("riwayat_transaksi.php")
    suspend fun getTransactionHistory(
        @Field("username") username: String
    ): TransactionHistoryResponse
}

data class BaseResponse(
    val success: Boolean,
    val message: String? = null,
    val username: String? = null,
    val saldo: String? = null
)

data class SaldoResponse(
    val success: Boolean,
    val message: String? = null,
    val saldo: String? = null
)

data class TransactionHistoryResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<TransactionData>? = null
)

data class TransactionData(
    val id: Int? = null,
    val type: String? = null,
    val amount: String? = null,
    val status: String? = null,
    val created_at: String? = null
)
