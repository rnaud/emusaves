package com.emusaves.data.remote.api

import com.emusaves.data.remote.model.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class SynologyApiClient(
    private var host: String,
    private var username: String,
    private var password: String,
    private var sid: String? = null
) {
    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val baseUrl: String get() = "http://$host:5000/webapi"

    suspend fun login(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = buildApiRequest(
                "entry.cgi",
                mapOf(
                    "api" to "SYNO.API.Auth",
                    "version" to "7",
                    "method" to "login",
                    "account" to username,
                    "password" to password,
                    "session" to "EmuSaves",
                    "format" to "cookie"
                )
            )
            
            val response = client.newCall(request).execute()
            val authResponse = gson.fromJson(response.body?.string(), SynologyAuthResponse::class.java)
            
            if (authResponse.success && authResponse.data?.sid != null) {
                sid = authResponse.data.sid
                Result.success(authResponse.data.sid)
            } else {
                Result.failure(IOException("Login failed: ${response.body?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = buildApiRequest(
                "entry.cgi",
                mapOf(
                    "api" to "SYNO.API.Auth",
                    "version" to "7",
                    "method" to "logout",
                    "session" to "EmuSaves"
                )
            )
            client.newCall(request).execute()
            sid = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listFiles(path: String): Result<List<SynologyFile>> = withContext(Dispatchers.IO) {
        try {
            val request = buildApiRequest(
                "entry.cgi",
                mapOf(
                    "api" to "SYNO.FileStation.List",
                    "version" to "2",
                    "method" to "list",
                    "path" to path,
                    "sort_by" to "name",
                    "sort_direction" to "asc"
                )
            )
            
            val response = client.newCall(request).execute()
            val fileResponse = gson.fromJson(response.body?.string(), SynologyFileResponse::class.java)
            
            if (fileResponse.success && fileResponse.data?.files != null) {
                Result.success(fileResponse.data.files)
            } else {
                Result.failure(IOException("List files failed: ${response.body?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createFolder(path: String, name: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fullPath = "$path/$name"
            val request = buildApiRequest(
                "entry.cgi",
                mapOf(
                    "api" to "SYNO.FileStation.CreateFolder",
                    "version" to "3",
                    "method" to "create",
                    "folder_path" to "[${"\"$fullPath\""}]",
                    "force_parent" to "true"
                )
            )
            
            val response = client.newCall(request).execute()
            val folderResponse = gson.fromJson(response.body?.string(), SynologyCreateFolderResponse::class.java)
            
            if (folderResponse.success && folderResponse.data != null) {
                Result.success(folderResponse.data.path)
            } else {
                Result.failure(IOException("Create folder failed: ${response.body?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadFile(
        localFileContent: ByteArray,
        remotePath: String,
        fileName: String,
        overwrite: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/entry.cgi?api=SYNO.FileStation.Upload&version=2&method=upload&path=$remotePath"

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, localFileContent.toRequestBody("application/octet-stream".toMediaType()))
                .addFormDataPart("overwrite", overwrite.toString())
                .build()

            val request = Request.Builder()
                .url(url)
                .addHeader("Cookie", "id=$sid")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                Result.success("$remotePath/$fileName")
            } else {
                Result.failure(IOException("Upload failed: ${response.code} ${response.body?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildApiRequest(path: String, params: Map<String, String>): Request {
        val queryParams = params.entries.joinToString("&") { "${it.key}=${it.value}" }
        val url = "$baseUrl/$path?$queryParams"
        
        return Request.Builder()
            .url(url)
            .get()
            .build()
    }

    fun updateConfig(host: String, username: String, password: String) {
        this.host = host
        this.username = username
        this.password = password
        this.sid = null
    }
}
