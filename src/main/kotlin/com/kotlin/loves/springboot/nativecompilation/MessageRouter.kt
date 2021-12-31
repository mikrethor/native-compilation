package com.kotlin.loves.springboot.nativecompilation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration(proxyBeanMethods = false)
class MessageRouter {

    @Bean
    fun route(messageHandler: MessageHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(GET("/messages"), messageHandler::getAllMessages)
            .andRoute(GET("/messages/{id}"), messageHandler::getMessageById)
            .andRoute(POST("/messages").and(accept(MediaType.APPLICATION_JSON)), messageHandler::create)
            .andRoute(DELETE("/messages/{id}"), messageHandler::delete)
            .andRoute(PUT("/messages").and(accept(MediaType.APPLICATION_JSON)), messageHandler::modify)
    }
}