package com.emusaves.data.remote.model

import com.google.gson.annotations.SerializedName

// Auth Models
data class SynologyAuthRequest(
    @SerializedName("account") val account: String,
    @SerializedName("password") val password: String,
    @SerializedName("session") val session: String = "EmuSaves",
    @SerializedName("format") val format: String = "cookie"
)

data class SynologyAuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SynologyAuthData?
)

data class SynologyAuthData(
    @SerializedName("sid") val sid: String?
)

// File API Models  
data class SynologyFileListRequest(
    @SerializedName("path") val path: String,
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("limit") val limit: Int = -1,
    @SerializedName("sort_by") val sortBy: String = "name",
    @SerializedName("sort_direction") val sortDirection: String = "asc"
)

data class SynologyFileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SynologyFileData?
)

data class SynologyFileData(
    @SerializedName("offset") val offset: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("files") val files: List<SynologyFile>?
)

data class SynologyFile(
    @SerializedName("name") val name: String,
    @SerializedName("path") val path: String,
    @SerializedName("isdir") val isDir: Boolean,
    @SerializedName("size") val size: Long,
    @SerializedName("mtime") val modifiedTime: Long
)

// Upload API Models
data class SynologyUploadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SynologyUploadData?
)

data class SynologyUploadData(
    @SerializedName("blksize") val blockSize: Int,
    @SerializedName("size") val size: Long,
    @SerializedName("dest") val dest: String,
    @SerializedName("dest_stated") val destStated: String,
    @SerializedName("path") val path: String,
    @SerializedName("progress") val progress: Boolean
)

// Create Folder API
data class SynologyCreateFolderRequest(
    @SerializedName("folder_path") val folderPath: String,
    @SerializedName("name") val name: String,
    @SerializedName("force_parent") val forceParent: Boolean = true
)

data class SynologyCreateFolderResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SynologyCreateFolderData?
)

data class SynologyCreateFolderData(
    @SerializedName("folder_path") val folderPath: String,
    @SerializedName("name") val name: String,
    @SerializedName("path") val path: String
)
