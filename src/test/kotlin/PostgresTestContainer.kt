package shop

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.testcontainers.containers.PostgreSQLContainer

@AutoConfigureTestDatabase
class PostgresTestContainer : BeforeAllCallback, AfterAllCallback {

    private val postgresqlContainer = MyPostgreSQLContainer("postgres:12-alpine")

    override fun beforeAll(context: ExtensionContext?) {
        postgresqlContainer.start()
    }

    override fun afterAll(context: ExtensionContext?) {
        postgresqlContainer.stop()
    }
}

class MyPostgreSQLContainer(imageName: String) : PostgreSQLContainer<MyPostgreSQLContainer>(imageName)
