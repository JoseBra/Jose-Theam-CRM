package shop.model

import java.util.*

data class Customer(
        val name: String,
        val surname: String,
        val customerId: CustomerID?
)

class CustomerID(val id: String) {
    companion object {
        fun generate(): CustomerID {
            return CustomerID(UUID.randomUUID().toString())
        }
    }
}
