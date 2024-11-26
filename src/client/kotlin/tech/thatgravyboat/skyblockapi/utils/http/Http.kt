package tech.thatgravyboat.skyblockapi.utils.http

import com.google.gson.Gson
import tech.thatgravyboat.skyblockapi.utils.json.Json
import java.io.InputStream
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

typealias StreamResponse = java.net.http.HttpResponse<InputStream>

object Http {

    private val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(10.seconds.toJavaDuration())
        .build()

    private fun String.urlEncode() = URLEncoder.encode(this, "UTF-8")

    private fun createUrl(url: String, queries: Map<String, Any> = mapOf()): URI {
        val query = queries.entries.joinToString("&") {
            "${it.key.urlEncode()}=${it.value.toString().urlEncode()}"
        }
        return URI.create("$url?$query")
    }

    private fun connect(
        url: String,
        timeout: Int = 10000,
        queries: Map<String, Any> = mapOf(),
        headers: Map<String, String> = mapOf(),
        factory: HttpRequest.Builder.() -> Unit = {},
    ): StreamResponse {
        require(url.startsWith("https://")) { "Only HTTPS is supported" }

        return client.send(
            HttpRequest.newBuilder(createUrl(url, queries)).apply {
                header("User-Agent", "SkyBlock API")
                headers.forEach(::header)
                timeout(Duration.ofMillis(timeout.toLong()))
                factory()
            }.build(),
            BodyHandlers.ofInputStream(),
        )
    }

    suspend fun <T : Any> head(
        url: String,
        timeout: Int = 10000,
        queries: Map<String, Any> = mapOf(),
        headers: Map<String, String> = mapOf(),
        handler: suspend HttpResponse.() -> T,
    ): T {
        val response = connect(url, timeout, queries, headers) {
            HEAD()
        }
        return handler(HttpResponse(response.statusCode(), response.headers().map(), response.body()))
    }

    /**
     * This will perform a GET request on a provided url and return a response that is processed by a provided handler.
     *
     * @param url: The URL to send the GET request to
     * @param queries: The queries to append to the URL
     * @param timeout: The timeout in milliseconds
     * @param headers: The headers to send with the request
     * @param handler: The handler to process the response
     * @return: The data returned by the handler
     */
    suspend fun <T : Any> get(
        url: String,
        queries: Map<String, Any> = mapOf(),
        timeout: Int = 10000,
        headers: Map<String, String> = mapOf(),
        handler: suspend HttpResponse.() -> T,
    ): T {
        val response = connect(url, timeout, queries, headers) {
            GET()
        }
        return handler(HttpResponse(response.statusCode(), response.headers().map(), response.body()))
    }

    /**
     * This will perform a GET request on a provided url and return data as a Result.
     *
     * @param url: The URL to send the GET request to
     * @param gson: The Gson instance to parse the response
     * @param errorFactory: The factory to create an error from the response if it is not successful
     * @param queries: The queries to append to the URL
     * @param timeout: The timeout in milliseconds
     * @param headers: The headers to send with the request
     * @return: The data returned by the handler
     */
    suspend inline fun <reified T : Any> getResult(
        url: String,
        gson: Gson = Json.gson,
        crossinline errorFactory: ((String) -> Exception) = ::RuntimeException,
        queries: Map<String, Any> = mapOf(),
        timeout: Int = 10000,
        headers: Map<String, String> = mapOf(),
    ): Result<T> = try {
        get(url = url, queries = queries, timeout = timeout, headers = headers) {
            if (isOk) {
                Result.success(asJson<T>(gson))
            } else {
                Result.failure(errorFactory(asText()))
            }
        }
    } catch (e: Exception) {
        Result.failure(errorFactory(e.message ?: "Unknown error"))
    }

    /**
     * This will perform a POST request on a provided url and return a response that is processed by a provided handler.
     *
     * @param url: The URL to send the POST request to
     * @param timeout: The timeout in milliseconds
     * @param queries: The queries to append to the URL
     * @param headers: The headers to send with the request
     * @param body: The body to send with the request
     * @param handler: The handler to process the response
     */
    suspend fun <T : Any> post(
        url: String,
        timeout: Int = 10000,
        queries: Map<String, Any> = mapOf(),
        headers: Map<String, String> = mapOf(),
        body: String,
        handler: suspend HttpResponse.() -> T,
    ): T {
        val connection = connect(url, timeout, queries, headers) {
            POST(HttpRequest.BodyPublishers.ofString(body))
        }
        return connection.body().use {
            handler(HttpResponse(connection.statusCode(), connection.headers().map(), it))
        }
    }

    /**
     * This will perform a POST request on a provided url and return a response that is processed by a provided handler.
     *
     * @param url: The URL to send the POST request to
     * @param timeout: The timeout in milliseconds
     * @param queries: The queries to append to the URL
     * @param headers: The headers to send with the request
     * @param gson: The Gson instance to parse the response
     * @param body: The body to send with the request
     * @param handler: The handler to process the response
     * @return: The data returned by the handler
     */
    suspend fun <T : Any> post(
        url: String,
        timeout: Int = 10000,
        queries: Map<String, Any> = mapOf(),
        headers: Map<String, String> = mapOf(),
        gson: Gson = Json.gson,
        body: Any,
        handler: suspend HttpResponse.() -> T,
    ): T {
        val newHeaders = headers.toMutableMap()
        newHeaders["Content-Type"] = "application/json"
        return post(url, timeout, queries, newHeaders, gson.toJson(body), handler)
    }
}
