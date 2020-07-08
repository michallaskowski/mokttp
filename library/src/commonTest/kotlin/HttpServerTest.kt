package dev.michallaskowski.mokttp

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
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
        server.stop()
        client.close()
    }

    @Test
    fun returnsResponsePerPath() = runTest {
        server.router = object: Router {
            override fun handleRequest(request: Request): Response {
                if (request.path != "/test") {
                    return Response(404, emptyMap(), null, null)
                }
                return Response(200, emptyMap(), Data("test"), "text/plain")
            }
        }

        server.start(8080)

        val response = client.get<String>("http://localhost:8080/test")
        assertEquals("test", response)

        val httpResponse = client.get<HttpResponse>("http://localhost:8080/notTest")
        assertEquals(HttpStatusCode.NotFound, httpResponse.status)
    }

    @Test
    fun returnsHeaders() = runTest {
        server.router = object: Router {
            override fun handleRequest(request: Request): Response {
                return Response(
                    200,
                    mapOf("customHeader" to "customHeaderValue"),
                    Data("test"),
                    "text/plain"
                )
            }
        }

        server.start(8080)

        val httpResponse = client.get<HttpResponse>("http://localhost:8080/anything")
        assertEquals( HttpStatusCode.OK, httpResponse.status)
        assertEquals("customHeaderValue", httpResponse.headers["customHeader"])
    }
}