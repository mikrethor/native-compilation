package com.kotlin.loves.springboot.nativecompilation

import com.kotlin.loves.springboot.myfirstreactiveapp.PostgresSQLExtension
import com.kotlin.loves.springboot.myfirstreactiveapp.R2dbcConfig
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.LogConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import java.util.*
import java.util.stream.Collectors

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresSQLExtension::class)
@ContextConfiguration(initializers = [PostgresSQLExtension.Initializer::class])
@Import(R2dbcConfig::class)
class MessagesITTest {

    @LocalServerPort
    private var port = 0

    lateinit var requestSpecification: RequestSpecification

    @Autowired
    lateinit var messageRepository: MessageRepository

    @BeforeEach
    fun beforeEach() {
        val logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)
        val config = RestAssuredConfig.config().logConfig(logConfig)

        requestSpecification = RequestSpecBuilder().setBasePath("/").setPort(port).setRelaxedHTTPSValidation().setConfig(config).build()
    }

    @Test
    fun `Get a specific message`() {
        val response = Given {
            headers(emptyMap<String, String>())
            contentType(ContentType.JSON)
            spec(requestSpecification)
        } When {
            get("/messages/7cff6a38-a7cc-4478-883a-3edfa0232bca")
        } Then {
            statusCode(200)
        } Extract {
            asPrettyString()
        }

        assertThat(response).isEqualTo(
            """{
    "id": "7cff6a38-a7cc-4478-883a-3edfa0232bca",
    "message": "Hello Reader!"
}"""
        )
    }

    @Test
    fun `Get all messages`() {
        messageRepository.deleteAll().block()
        val messages = messageRepository.saveAll(
            listOf(
                Message(message = "Hello !"),
                Message(message = "Hello Reader!")
            )
        )
            .toStream().collect(Collectors.toList())

        val response = Given {
            headers(emptyMap<String, String>())
            contentType(ContentType.JSON)
            spec(requestSpecification)
        } When {
            get("/messages")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<Message>::class.java);
        }

        assertThat(response.size).isEqualTo(2)
        assertThat(response[0]).isEqualTo(messages[0])
        assertThat(response[1]).isEqualTo(messages[1])
    }

    @Test
    fun `Delete a message`() {
        val response = Given {
            headers(emptyMap<String, String>())
            contentType(ContentType.JSON)
            spec(requestSpecification)
        } When {
            delete("/messages/c2c10312-6b64-478c-be14-f227eea3a767")
        } Then {
            statusCode(200)
        } Extract {
            asPrettyString()
        }
        assertThat(response).isEmpty()
    }

    @Test
    fun `Create a message`() {
        val body = """{
        "message": "I have been created"
    }"""
        val response = Given {
            headers(emptyMap<String, String>())
            contentType(ContentType.JSON)
            spec(requestSpecification)
            body(body)
        } When {
            post("/messages")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Message::class.java)
        }
        assertThat(response.message).isEqualTo("I have been created")
        assertThat(response.id).isNotNull
    }

    @Test
    fun `Modify a message`() {
        messageRepository.deleteAll().block()
        val (id, _) = messageRepository.save(Message(message = "My message to modify")).blockOptional().get()


        val body = """{
        "id": "$id",
        "message": "My modified message"
    }"""

        val response = Given {
            headers(emptyMap<String, String>())
            contentType(ContentType.JSON)
            spec(requestSpecification)
            body(body)
        } When {
            put("/messages")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Message::class.java)
        }
        assertThat(response.message).isEqualTo("My modified message")
        assertThat(response.id).isNotNull.isEqualTo(id)
    }
}