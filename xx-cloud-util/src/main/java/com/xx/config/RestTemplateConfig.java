package com.xx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Agao
 * @date 2024/10/16 10:53
 */
@Configuration
public class RestTemplateConfig {
    /**
     * 读取超时时间 10s
     */
    private static final int READ_TIMEOUT = 10000;
    /**
     * 连接超时时间 10s
     */
    private static final int CONNECT_TIMEOUT = 10000;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory simpleClientHttpRequestFactory) {
        return new RestTemplate(simpleClientHttpRequestFactory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(READ_TIMEOUT);
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        return factory;
    }
}
