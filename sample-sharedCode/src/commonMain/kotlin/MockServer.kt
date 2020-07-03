package dev.michallaskowski.mokttp.sample.shared

import dev.michallaskowski.mokttp.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class MockServer {
    private val server = HttpServer()
    private val router = MockRouter()

    init {
        server.router = router
    }

    fun start(port: Int = 8080) {
        server.start(port)
    }
}

@Serializable
internal data class Model(val login: String, val contributions: Int)

class MockRouter: Router {
    override fun handleRequest(request: Request): Response {
        if (request.method == "GET" && request.path?.startsWith("/repos/") == true) {
            val data = Json(JsonConfiguration.Stable).stringify(
                Model.serializer().list,
                listOf(Model("test_from_shared_code", 42)))
            return Response(200, emptyMap(), Data(fromString = data), "application/json")
        } else {
            return Response(404, emptyMap(), null, null)
        }
    }
}