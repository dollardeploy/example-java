package com.cheonghyun.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "react.proxy")
public class ReactProperties {
    private boolean useReactBundle;
    private String host;
    private int port;
    private boolean ssl;

    public boolean isUseReactBundle() {
        return useReactBundle;
    }

    public void setUseReactBundle(boolean useReactBundle) {
        this.useReactBundle = useReactBundle;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}
