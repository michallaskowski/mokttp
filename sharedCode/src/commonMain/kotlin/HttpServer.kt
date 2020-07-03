package dev.michallaskowski.mockttp

expect class Data

data class Request(
    val method: String?,
    val path: String?,
    val headers: Map<String, List<String>>,
    val body: Data?)

data class Response(val status: Int, val headers: Map<String, String>, val body: Data?, val contentType: String?)

interface Router {
    // TODO: handle asynchronously (add suspend)
    fun handleRequest(request: Request): Response
}

expect class HttpServer {
    var router: Router?
    fun start(port: Int)
}