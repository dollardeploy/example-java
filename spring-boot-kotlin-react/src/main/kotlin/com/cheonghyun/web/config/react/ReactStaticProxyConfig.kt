package com.cheonghyun.web.config.react

import com.cheonghyun.web.config.properties.ReactProperties
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Configuration
@Order(1)
class ReactStaticProxyConfig(
    private val reactProperties: ReactProperties
) : OncePerRequestFilter() {

    private val client = OkHttpClient()
    private val pathMatcher = AntPathMatcher()

    private val whitelistPatterns = listOf(
        "/static/js/**",
        "/static/css/**",
        "/*.hot-update.json",
        "/*.hot-update.js"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI

        val shouldProxy = whitelistPatterns.any { pattern ->
            pathMatcher.match(pattern, uri)
        }

        if (!shouldProxy) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val hostUrl = if (reactProperties.ssl == true) {
                "https://${reactProperties.host}:${reactProperties.port}"
            } else {
                "http://${reactProperties.host}:${reactProperties.port}"
            }

            val targetUrl = hostUrl + uri +
                    (request.queryString?.let { "?$it" } ?: "")

            val requestBuilder = Request.Builder().url(targetUrl).get()

            val skipHeaders = listOf(
                "host", "connection", "transfer-encoding",
                "content-encoding", "content-length", "accept-encoding"
            )

            val headerNames = request.headerNames
            while (headerNames.hasMoreElements()) {
                val headerName = headerNames.nextElement()
                if (headerName.lowercase() !in skipHeaders) {
                    val headerValues = request.getHeaders(headerName)
                    while (headerValues.hasMoreElements()) {
                        requestBuilder.addHeader(headerName, headerValues.nextElement())
                    }
                }
            }

            val proxiedResponse = client.newCall(requestBuilder.build()).execute()
            response.status = proxiedResponse.code

            proxiedResponse.headers.names().forEach { name ->
                if (name.lowercase() !in listOf("transfer-encoding", "content-encoding")) {
                    proxiedResponse.headers.values(name).forEach { value ->
                        response.addHeader(name, value)
                    }
                }
            }

            val body = proxiedResponse.body?.bytes()
            if (body != null) {
                response.setContentLength(body.size)
                response.outputStream.write(body)
            } else {
                response.setContentLength(0)
            }

            proxiedResponse.close()
        } catch (e: IOException) {
            response.status = 502
            response.writer.write("Proxy Error: ${e.message}")
        }
    }
}