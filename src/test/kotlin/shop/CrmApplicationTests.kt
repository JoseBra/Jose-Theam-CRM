package shop

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ExtendWith(PostgresTestContainer::class)
class CrmApplicationTests {

	@Test
	fun contextLoads() {
	}

}
