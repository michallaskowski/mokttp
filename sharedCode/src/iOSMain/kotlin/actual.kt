package dev.michallaskowski.mockttp

import gcdWebServer.*
import platform.Foundation.*
import kotlinx.cinterop.*
import platform.posix.memcpy
import kotlin.native.concurrent.freeze

actual typealias Data = NSData

@Suppress("UNCHECKED_CAST")
actual class HttpServer {
    private val httpServer = GCDWebServer()
    actual fun start(port: Int) {
        httpServer.startWithPort(port.toULong(), null)
    }

    actual var router: Router? = null
        set(newRouter) {
            field = newRouter
            httpServer.removeAllHandlers()

            if (newRouter != null) {
                val matchBlock: GCDWebServerMatchBlock = { method: String?, url: NSURL?, headers: Map<Any?, *>?, path: String?, query: Map<Any?, *>? ->
                    GCDWebServerRequest(method!!, url!!, headers!!, path!!, query)
                }

                val processBlock: (GCDWebServerRequest?) -> GCDWebServerResponse = { gcdRequest ->
                    val body = (gcdRequest as? GCDWebServerDataRequest)?.data

                    val headers = (gcdRequest!!.headers as Map<String, String>).mapValues {
                        listOf(it.value)
                    }
                    val path = gcdRequest.path
                    val request = Request(gcdRequest.method, path, headers, body)

                    val response = newRouter.handleRequest(request)
                    if (response.body != null) {
                        GCDWebServerDataResponse(response.body, response.contentType ?: "")
                    } else {
                        val gcdResponse = GCDWebServerResponse()
                        gcdResponse.statusCode = response.status.toLong()
                        gcdResponse
                    }
                }

                httpServer.addHandlerWithMatchBlock(
                    matchBlock = matchBlock.freeze(),
                    processBlock = processBlock.freeze())
            }
        }
}
