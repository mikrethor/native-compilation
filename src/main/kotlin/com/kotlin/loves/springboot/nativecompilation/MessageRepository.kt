package com.kotlin.loves.springboot.nativecompilation

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface MessageRepository : ReactiveCrudRepository<Message, UUID> {

    @Query("select id, message from message m where m.message = :message")
    fun findByMessage(message: String): Mono<Message>
}

