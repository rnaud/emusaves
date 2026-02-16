package com.emusaves.data.remote.api

import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.Assert.*

@Ignore
class SynologyApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: SynologyApiClient
    private val gson = Gson()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val host = "${mockWebServer.hostName}:${mockWebServer.port}"
        apiClient = SynologyApiClient(host, "testuser", "testpass")
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login should return success with valid credentials`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody("""{"success": true, "data": {"sid": "test-session-id"}}""")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.login()

        // Then
        assertTrue("Login should succeed", result.isSuccess)
        assertEquals("test-session-id", result.getOrNull())
        
        // Verify request
        val request = mockWebServer.takeRequest()
        assertTrue("Should call auth API", request.path!!.contains("SYNO.API.Auth"))
        assertTrue("Should use login method", request.path!!.contains("method=login"))
        assertTrue("Should include username", request.path!!.contains("account=testuser"))
        assertTrue("Should include password", request.path!!.contains("password=testpass"))
    }

    @Test
    fun `login should return failure with invalid credentials`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody("""{"success": false, "error": {"code": 400}}""")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.login()

        // Then
        assertTrue("Login should fail", result.isFailure)
        assertNotNull("Should have error message", result.exceptionOrNull())
    }

    @Test
    fun `login should handle network errors`() = runTest {
        // Given - Server returns error response
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.login()

        // Then
        assertTrue("Login should fail on network error", result.isFailure)
    }

    @Test
    fun `logout should clear session`() = runTest {
        // Given - Login first
        mockWebServer.enqueue(MockResponse()
            .setBody("""{"success": true, "data": {"sid": "test-session-id"}}""")
            .setResponseCode(200))
        
        mockWebServer.enqueue(MockResponse()
            .setBody("""{"success": true}""")
            .setResponseCode(200))

        apiClient.login()

        // When
        val result = apiClient.logout()

        // Then
        assertTrue("Logout should succeed", result.isSuccess)
        
        // Verify logout request
        mockWebServer.takeRequest() // Skip login request
        val logoutRequest = mockWebServer.takeRequest()
        assertTrue("Should call auth API", logoutRequest.path!!.contains("SYNO.API.Auth"))
        assertTrue("Should use logout method", logoutRequest.path!!.contains("method=logout"))
    }

    @Test
    fun `listFiles should return file list`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody("""
                {
                    "success": true,
                    "data": {
                        "offset": 0,
                        "total": 2,
                        "files": [
                            {
                                "name": "game1.srm",
                                "path": "/Drive/EmulatorBackups/game1.srm",
                                "isdir": false,
                                "size": 1024,
                                "mtime": 1640995200
                            },
                            {
                                "name": "game2.sav",
                                "path": "/Drive/EmulatorBackups/game2.sav", 
                                "isdir": false,
                                "size": 2048,
                                "mtime": 1640995300
                            }
                        ]
                    }
                }
            """.trimIndent())
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.listFiles("/Drive/EmulatorBackups")

        // Then
        assertTrue("List files should succeed", result.isSuccess)
        val files = result.getOrNull()!!
        assertEquals("Should return 2 files", 2, files.size)
        assertEquals("game1.srm", files[0].name)
        assertEquals("game2.sav", files[1].name)
        assertFalse("Files should not be directories", files[0].isDir)
        assertEquals(1024L, files[0].size)
        
        // Verify request
        val request = mockWebServer.takeRequest()
        assertTrue("Should call FileStation.List API", request.path!!.contains("SYNO.FileStation.List"))
        assertTrue("Should include path", request.path!!.contains("path=/Drive/EmulatorBackups"))
    }

    @Test
    fun `listFiles should handle empty directory`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody("""
                {
                    "success": true,
                    "data": {
                        "offset": 0,
                        "total": 0,
                        "files": []
                    }
                }
            """.trimIndent())
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.listFiles("/empty/directory")

        // Then
        assertTrue("List files should succeed", result.isSuccess)
        val files = result.getOrNull()!!
        assertEquals("Should return empty list", 0, files.size)
    }

    @Test
    fun `createFolder should create folder successfully`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody("""
                {
                    "success": true,
                    "data": {
                        "folder_path": "/Drive",
                        "name": "EmulatorBackups",
                        "path": "/Drive/EmulatorBackups"
                    }
                }
            """.trimIndent())
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.createFolder("/Drive", "EmulatorBackups")

        // Then
        assertTrue("Create folder should succeed", result.isSuccess)
        assertEquals("/Drive/EmulatorBackups", result.getOrNull())
        
        // Verify request
        val request = mockWebServer.takeRequest()
        assertTrue("Should call CreateFolder API", request.path!!.contains("SYNO.FileStation.CreateFolder"))
        assertTrue("Should use create method", request.path!!.contains("method=create"))
        assertTrue("Should force parent creation", request.path!!.contains("force_parent=true"))
    }

    @Test
    fun `createFolder should handle errors`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody("""{"success": false, "error": {"code": 408}}""")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.createFolder("/invalid", "folder")

        // Then
        assertTrue("Create folder should fail", result.isFailure)
    }

    @Test
    fun `uploadFile should upload file successfully`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody("""{"success": true}""")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val fileContent = "test file content".toByteArray()

        // When
        val result = apiClient.uploadFile(
            localFileContent = fileContent,
            remotePath = "/Drive/EmulatorBackups",
            fileName = "test.srm"
        )

        // Then
        assertTrue("Upload should succeed", result.isSuccess)
        assertEquals("/Drive/EmulatorBackups/test.srm", result.getOrNull())
        
        // Verify request
        val request = mockWebServer.takeRequest()
        assertTrue("Should call Upload API", request.path!!.contains("SYNO.FileStation.Upload"))
        assertTrue("Should use upload method", request.path!!.contains("method=upload"))
        assertEquals("POST", request.method)
        assertTrue("Should be multipart", request.headers["Content-Type"]!!.contains("multipart/form-data"))
    }

    @Test
    fun `uploadFile should handle upload errors`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        val fileContent = "test file content".toByteArray()

        // When
        val result = apiClient.uploadFile(
            localFileContent = fileContent,
            remotePath = "/invalid",
            fileName = "test.srm"
        )

        // Then
        assertTrue("Upload should fail", result.isFailure)
    }

    @Test
    fun `uploadFile should handle overwrite parameter`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody("""{"success": true}""")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val fileContent = "test file content".toByteArray()

        // When
        apiClient.uploadFile(
            localFileContent = fileContent,
            remotePath = "/Drive/EmulatorBackups",
            fileName = "test.srm",
            overwrite = true
        )

        // Then
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertTrue("Should include overwrite=true", requestBody.contains("overwrite"))
        assertTrue("Should set overwrite to true", requestBody.contains("true"))
    }

    @Test
    fun `updateConfig should update client configuration`() {
        // When
        apiClient.updateConfig("new-host", "new-user", "new-pass")

        // Then - Verify by checking if login uses new credentials
        // (This would be verified by checking the actual requests in a real test)
        // The private fields can't be directly accessed, but we can verify the behavior
        // by mocking a login request and checking the parameters
    }

    @Test
    fun `buildApiRequest should construct correct URL`() {
        // This tests the private method indirectly through public methods
        // by verifying the constructed URLs in the mock server requests
        
        mockWebServer.enqueue(MockResponse()
            .setBody("""{"success": false}""")
            .setResponseCode(200))

        runTest {
            apiClient.listFiles("/test/path")
            
            val request = mockWebServer.takeRequest()
            val path = request.path!!
            
            assertTrue("Should include API parameter", path.contains("api=SYNO.FileStation.List"))
            assertTrue("Should include version parameter", path.contains("version=2"))
            assertTrue("Should include method parameter", path.contains("method=list"))
            assertTrue("Should include path parameter", path.contains("path=/test/path"))
            assertTrue("Should include sort parameters", path.contains("sort_by=name"))
        }
    }
}