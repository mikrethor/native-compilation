package com.kotlin.loves.springboot.nativecompilation

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import java.util.*

@Component
class MessageHandler(val messageRepository: MessageRepository) {

    fun getAllMessages(request: ServerRequest) = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(messageRepository.findAll(), Message::class.java)
        .switchIfEmpty(ServerResponse.notFound().build())

    fun getMessageById(request: ServerRequest) = ServerResponse.ok()
        .body(messageRepository.findById(UUID.fromString(request.pathVariable("id"))), Message::class.java)
        .switchIfEmpty(ServerResponse.notFound().build())

    fun create(request: ServerRequest) =
        request.bodyToMono(Message::class.java)
            .flatMap { message ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(messageRepository.save(message), Message::class.java)
            }

    fun delete(request: ServerRequest) = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(messageRepository.deleteById(UUID.fromString(request.pathVariable("id"))), Void::class.java)
        .switchIfEmpty(ServerResponse.notFound().build())

    fun modify(request: ServerRequest) = request.bodyToMono(Message::class.java)
        .flatMap { message ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(messageRepository.save(message), Message::class.java)
        }
}