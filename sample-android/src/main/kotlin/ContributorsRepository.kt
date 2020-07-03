package dev.michallaskowski.kuiks.sample.android

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.*
import ru.gildor.coroutines.okhttp.await
import java.lang.Exception

@Serializable
data class Model(val login: String, val contributions: Int)

enum class Environment {
    MOCKED, SHARED_MOCK, ORIGINAL
}

class ContributorsRepository(val environment: Environment) {
    private val client = OkHttpClient()

    private fun url(): String {
        val baseUrl = when(environment) {
            Environment.MOCKED -> "http://localhost:8080"
            Environment.SHARED_MOCK -> "http://localhost:8081"
            Environment.ORIGINAL -> "https://api.github.com"
        }
        return "$baseUrl/repos/michallaskowski/kuiks/contributors"
    }

    suspend fun getContributors(): List<Model> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url())
            .build()

        var data: List<Model> = emptyList()
        try {
            val response = client.newCall(request).await()
            val responseBody = response.body?.string()
            response.close()

            if (responseBody != null) {
                data = Json(JsonConfiguration(ignoreUnknownKeys = true)).parse(
                    Model.serializer().list,
                    responseBody)
            }
        } catch (e: Exception) {
            print("Exception")
        }

        data
    }
}

