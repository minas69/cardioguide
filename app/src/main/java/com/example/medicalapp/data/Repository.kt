package com.example.medicalapp.data

import com.example.medicalapp.data.model.ResultResponse
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import com.example.medicalapp.data.Result.Success
import com.example.medicalapp.data.Result.Error
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.*
import java.io.File

object Repository {

    suspend fun post(values: Map<String, Any>): Result<ResultResponse> {
        val data = mutableListOf<HashMap<String, *>>()
        values.forEach { input ->
            data.add(hashMapOf("id" to input.key, "value" to input.value))
        }
        val request = DataRequest(data)
        val response = API.apiService.postData(request)
        if (response.isSuccessful) {
            val result = response.body() ?: return Error(Exception("null"))
            return Success(result)
        } else {
            val gson = Gson()
            val type = object : TypeToken<ErrorResponse>() {}.type
            val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()?.charStream(), type)
            return Error(Exception(errorResponse?.msg ?: "Error occurred"))
        }
    }

    suspend fun sendPhoto(photoPath: String): Result<String> {
        val file = File(photoPath)
        val body = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val response = API.apiService.sendPhoto(body)
        if (response.isSuccessful) {
            val result = response.body()?.id ?: return Error(Exception("null"))
            return Success(result)
        } else {
            val gson = Gson()
            val type = object : TypeToken<ErrorResponse>() {}.type
            val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()?.charStream(), type)
            return Error(Exception(errorResponse?.msg ?: "Error occurred"))
        }
    }

    suspend fun sendReview(recordId: String, aware: Boolean, helpful: Boolean, rate: Int): Result<Unit> {
        val response = API.apiService.sendReview(recordId, ReviewRequest(aware, helpful, rate))
        if (response.isSuccessful) {
            val result = response.body() ?: return Error(Exception("null"))
            return Success(result)
        } else {
            val gson = Gson()
            val type = object : TypeToken<ErrorResponse>() {}.type
            val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()?.charStream(), type)
            return Error(Exception(errorResponse?.msg ?: "Error occurred"))
        }
    }

}

data class DataRequest(val data: List<Map<String, *>>)

data class ErrorResponse(val msg: String)

data class PhotoResponse(val id: String)

data class ReviewRequest(val aware: Boolean, val helpful: Boolean, val rate: Int)

object API {
    private const val API_BASE_URL = "https://us-central1-score-school.cloudfunctions.net/api/"
//    private const val API_BASE_URL = "http://192.168.1.111:5001/score-school/us-central1/api/"

    private val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }
        )
        addNetworkInterceptor(AuthInterceptor())
    }

    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit: Retrofit = builder
        .client(httpClient.build())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    interface ApiService {
        @POST("post")
        suspend fun postData(@Body request: DataRequest): retrofit2.Response<ResultResponse>

        @Multipart
        @POST("photo")
        suspend fun sendPhoto(@Part photo: MultipartBody.Part): retrofit2.Response<PhotoResponse>

        @POST("rate")
        suspend fun sendReview(@Query("id") id: String, @Body request: ReviewRequest): retrofit2.Response<Unit>
    }

    class AuthInterceptor : Interceptor {

        private  val auth: FirebaseAuth by lazy { Firebase.auth }

        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            try {
                val user = auth.currentUser ?: throw Exception("user is not logged in.")
                val tokenResult = Tasks.await(user.getIdToken((false)))
                val token = tokenResult.token ?: throw Exception("idToken is null.")

                request = request
                    .newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()

                return chain.proceed(request)
            } catch (e: Exception) {
                throw IOException(e.message)
            }
        }

    }
}