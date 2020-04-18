package shop.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shop.utils.IdGenerator

@Configuration
class Configuration {
    @Bean
    fun idGenerator(): IdGenerator = IdGenerator()
}