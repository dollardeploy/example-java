package com.cheonghyun.web.config.react;

import com.cheonghyun.web.config.properties.ReactProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Configuration
@Order(1)
public class ReactStaticProxyConfig extends OncePerRequestFilter {
    private final OkHttpClient client = new OkHttpClient();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ReactProperties reactProperties;

    private final List<String> whitelistPatterns = Arrays.asList(
            "/static/js/**",
            "/static/css/**",
            "/*.hot-update.json",
            "/*.hot-update.js"
    );

    public ReactStaticProxyConfig(ReactProperties reactProperties) {
        this.reactProperties = reactProperties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String uri = request.getRequestURI();

        boolean shouldProxy = whitelistPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));

        if (!shouldProxy) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String hostUrl = (Boolean.TRUE.equals(reactProperties.getSsl()) ? "https" : "http") +
                    "://" + reactProperties.getHost() + ":" + reactProperties.getPort();

            String targetUrl = hostUrl + uri;
            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }

            Request.Builder requestBuilder = new Request.Builder()
                    .url(targetUrl)
                    .get();

            List<String> skipHeaders = Arrays.asList(
                    "host", "connection", "transfer-encoding",
                    "content-encoding", "content-length", "accept-encoding"
            );

            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (!skipHeaders.contains(headerName.toLowerCase())) {
                    Enumeration<String> headerValues = request.getHeaders(headerName);
                    while (headerValues.hasMoreElements()) {
                        requestBuilder.addHeader(headerName, headerValues.nextElement());
                    }
                }
            }

            try (Response proxiedResponse = client.newCall(requestBuilder.build()).execute()) {
                response.setStatus(proxiedResponse.code());

                for (String name : proxiedResponse.headers().names()) {
                    if (!Arrays.asList("transfer-encoding", "content-encoding").contains(name.toLowerCase())) {
                        for (String value : proxiedResponse.headers(name)) {
                            response.addHeader(name, value);
                        }
                    }
                }

                ResponseBody responseBody = proxiedResponse.body();
                if (responseBody != null) {
                    byte[] bodyBytes = responseBody.bytes();
                    response.setContentLength(bodyBytes.length);
                    response.getOutputStream().write(bodyBytes);
                } else {
                    response.setContentLength(0);
                }
            }
        } catch (IOException e) {
            response.setStatus(502);
            response.getWriter().write("Proxy Error: " + e.getMessage());
        }
    }
}
