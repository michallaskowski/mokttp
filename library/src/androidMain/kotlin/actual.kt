package dev.michallaskowski.mokttp

import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer

actual typealias Data = Buffer
actual fun Data(fromString: String): Data {
    return Buffer().writeUtf8(fromString)
}
actual fun Data.asString(): String {
    return readUtf8()
}

private class DispatcherImpl: Dispatcher() {
    var router: Router? = null

    override fun dispatch(request: RecordedRequest): MockResponse {
        val headers = request.headers.toMultimap()
        val body = request.body
        val path = request.path
        val mokRequest = Request(request.method, path, headers, body)
        val response = router?.handleRequest(mokRequest)

        return response?.let {
            MockResponse().apply {
                setResponseCode(it.status)
                setHeaders(it.headers.toHeaders())

                if (it.body != null) {
                    setBody(it.body)
                } else {
                    setHeader("Content-Length", 0)
                }
            }
        } ?: MockResponse()
    }

}

actual class HttpServer actual constructor() {
    private val dispatcher = DispatcherImpl()
    private val mockWebServer = MockWebServer()
    actual fun start(port: Int) {
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start(port)
    }

    actual var router: Router?
        get() = dispatcher.router
        set(newRouter) {
            dispatcher.router = newRouter
        }
}