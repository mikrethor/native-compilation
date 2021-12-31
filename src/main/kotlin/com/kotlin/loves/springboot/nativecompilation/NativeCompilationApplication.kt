package com.kotlin.loves.springboot.nativecompilation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.nativex.hint.NativeHint
import org.springframework.nativex.hint.TypeAccess
import org.springframework.nativex.hint.TypeHint

@TypeHint(types = [Message::class], access = [TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_METHODS])
@TypeHint(types = [SimpleR2dbcRepository::class, ReactiveCrudRepository::class], access = [TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_METHODS])
@SpringBootApplication
class NativeCompilationApplication

fun main(args: Array<String>) {
    runApplication<NativeCompilationApplication>(*args)
}
