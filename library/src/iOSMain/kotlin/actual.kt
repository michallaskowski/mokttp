package dev.michallaskowski.mokttp

import gcdWebServer.*
import platform.Foundation.*
import kotlinx.cinterop.*
import platform.posix.memcpy
import kotlin.native.concurrent.freeze

actual typealias Data = NSData

@Suppress("CAST_NEVER_SUCCEEDS")
actual fun Data(fromString: String): Data {
    return (fromString as NSString).dataUsingEncoding(NSUTF8StringEncoding)!!
}

@Suppress("CAST_NEVER_SUCCEEDS")
actual fun Data.asString(): String {
    return NSString.create(this, NSUTF8StringEncoding)!! as String
}

@Suppress("UNCHECKED_CAST")
actual class HttpServer actual constructor() {
    private val httpServer = GCDWebServer()
    actual fun start(port: Int) {
        httpServer.startWithPort(port.toULong(), null)
    }

    actual fun stop() {
        httpServer.stop()
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
                    val gcdResponse: GCDWebServerResponse
                    if (response.body != null) {
                        gcdResponse = GCDWebServerDataResponse(response.body, response.contentType ?: "")
                    } else {
                        gcdResponse = GCDWebServerResponse()
                        gcdResponse.statusCode = response.status.toLong()
                        gcdResponse
                    }

                    response.headers.forEach {
                        gcdResponse.setValue(it.value, it.key)
                    }

                    gcdResponse
                }

                httpServer.addHandlerWithMatchBlock(
                    matchBlock = matchBlock.freeze(),
                    processBlock = processBlock.freeze())
            }
        }
}
