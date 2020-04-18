package shop.utils

import java.util.*

class IdGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}