package com.herokuapp.restfulbooker.config;

import com.herokuapp.restfulbooker.model.TokenCredentials;
import com.herokuapp.restfulbooker.service.BookerClient;
import com.herokuapp.restfulbooker.service.BookerService;
import feign.Logger;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = BookerClient.class)
@ImportAutoConfiguration({FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
public class BookerConfiguration {

    @Bean
    BookerService bookerService(BookerClient bookerClient, TokenCredentials tokenCredentials){
        return new BookerService(bookerClient, tokenCredentials);
    }

    @Bean
    TokenCredentials tokenCredentials(){
        return new TokenCredentials();
    }

}

