package com.kotlin.loves.springboot.myfirstreactiveapp

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer


class PostgresSQLExtension : BeforeAllCallback, AfterAllCallback {

    internal class SpecifiedPostgresSQLContainer(imageName: String) : PostgreSQLContainer<SpecifiedPostgresSQLContainer>(imageName)

    companion object {
        private val postgresSQLContainer = SpecifiedPostgresSQLContainer("postgres:14.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa")
    }

    override fun beforeAll(extensionContext: ExtensionContext) {
        postgresSQLContainer.start()
    }

    override fun afterAll(extensionContext: ExtensionContext) {
        postgresSQLContainer.stop()
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.r2dbc.url=${postgresSQLContainer.jdbcUrl.replace("jdbc", "r2dbc")}",
                "spring.r2dbc.username=${postgresSQLContainer.username}",
                "spring.r2dbc.password=${postgresSQLContainer.password}"
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}
