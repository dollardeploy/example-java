package com.cheonghyun.web.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "react.proxy")
class ReactProperties {
    var useReactBundle: Boolean? = null
    var host: String? = null
    var port: Int? = null
    var ssl: Boolean? = null
}