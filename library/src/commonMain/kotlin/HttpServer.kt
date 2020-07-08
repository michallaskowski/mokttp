package dev.michallaskowski.mokttp

expect class Data
expect fun Data(fromString: String): Data
expect fun Data.asString(): String

data class Request(
    val method: String?,
    val path: String?,
    val headers: Map<String, List<String>>,
    val body: Data?)

data class Response(val status: Int, val headers: Map<String, String>, val body: Data?, val contentType: String?)

interface Router {
    // TODO: handle asynchronously (add suspend, should work in Kotlin Native > 1.4-M2)
    fun handleRequest(request: Request): Response
}

expect class HttpServer() {
    var router: Router?
    fun start(port: Int)
    fun stop()
}