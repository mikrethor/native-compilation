package com.kotlin.loves.springboot.nativecompilation

import org.springframework.data.annotation.Id
import java.util.*

data class Message(@Id val id: UUID?=null, val message: String="")