package com.cheonghyun.web.config.react

import com.cheonghyun.web.config.properties.ReactProperties
import io.undertow.server.DefaultByteBufferPool
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.servlet.api.DeploymentInfo
import io.undertow.websockets.WebSocketConnectionCallback
import io.undertow.websockets.WebSocketProtocolHandshakeHandler
import io.undertow.websockets.client.WebSocketClient
import io.undertow.websockets.client.WebSocketClientNegotiation
import io.undertow.websockets.core.*
import org.slf4j.LoggerFactory
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.xnio.IoFuture
import org.xnio.OptionMap
import org.xnio.Xnio
import java.net.URI

@Configuration
class ReactWebSocketProxyConfig(
    private val reactProperties: ReactProperties
) {
    private val log = LoggerFactory.getLogger(ReactWebSocketProxyConfig::class.java)

    @Bean
    fun websocketProxyCustomizer(): UndertowDeploymentInfoCustomizer {
        return UndertowDeploymentInfoCustomizer { deploymentInfo: DeploymentInfo ->
            deploymentInfo.addInitialHandlerChainWrapper { next: HttpHandler ->
                HttpHandler { exchange: HttpServerExchange ->
                    if (exchange.requestPath.startsWith("/ws")) {
                        handleWebSocketProxy(exchange)
                    } else {
                        next.handleRequest(exchange)
                    }
                }
            }
        }
    }

    private fun handleWebSocketProxy(exchange: HttpServerExchange) {
        val worker = Xnio.getInstance().createWorker(OptionMap.EMPTY)
        val pool = DefaultByteBufferPool(true, 1024)
        val optionMap = OptionMap.EMPTY

        val hostUrl = if (reactProperties.ssl == true) {
            "wss://${reactProperties.host}:${reactProperties.port}"
        } else {
            "ws://${reactProperties.host}:${reactProperties.port}"
        }

        val targetUri = URI("${hostUrl}${exchange.requestURI}")

        val handler = WebSocketProtocolHandshakeHandler(
            WebSocketConnectionCallback { _, clientChannel ->
                val negotiation = object : WebSocketClientNegotiation(
                    emptyList(),
                    emptyList()
                ) {}

                val future = WebSocketClient.connect(
                    worker,
                    pool,
                    optionMap,
                    targetUri,
                    WebSocketVersion.V13,
                    negotiation
                )

                future.addNotifier(IoFuture.Notifier<WebSocketChannel, Void?> { notifierFuture, _ ->
                    try {
                        val serverChannel = notifierFuture.get()
                        setupProxyChannel(clientChannel, serverChannel) // Client to Server
                        setupProxyChannel(serverChannel, clientChannel) // Server to Client

                    } catch (e: Exception) {
                        log.error("handleWebSocketProxy.IoFuture.Notifier error", e)
                        clientChannel.sendClose()
                    }
                }, null)
            }
        )

        handler.handleRequest(exchange)
    }

    private fun setupProxyChannel(
        from: WebSocketChannel,
        to: WebSocketChannel
    ) {
        from.receiveSetter.set(object : AbstractReceiveListener() {
            override fun onFullTextMessage(channel: WebSocketChannel, message: BufferedTextMessage) {
                WebSockets.sendText(message.data, to, null)
            }

            override fun onFullBinaryMessage(channel: WebSocketChannel, message: BufferedBinaryMessage) {
                WebSockets.sendBinary(message.data.resource, to, null)
            }

            override fun onClose(webSocketChannel: WebSocketChannel?, channel: StreamSourceFrameChannel?) {
                to.sendClose()
            }
        })

        from.resumeReceives()
    }
}
