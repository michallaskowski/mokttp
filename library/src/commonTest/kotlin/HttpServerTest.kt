package dev.michallaskowski.mokttp

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpServerTest: BaseTest() {

    private lateinit var server: HttpServer
    private lateinit var client: HttpClient

    @BeforeTest
    fun setup() = runTest {
        server = HttpServer()
        client = HttpClient()
    }

    @AfterTest
    fun teardown() = runTest {
        // todo: server close
        client.close()
    }

    @Test
    fun someTest() = runTest {
        assertEquals(1, 1)
    }

    @Test
    fun returns200OkForProperPath() = runTest {
        server.router = object: Router {
            override fun handleRequest(request: Request): Response {
                if (request.path != "/test") {
                    return Response(404, emptyMap(), null, null)
                }
                return Response(200, emptyMap(), Data("test"), null)
            }
        }

        server.start(8080)

        val response = client.get<String>("http://localhost:8080/test")
        assertEquals(response, "test")
    }

}