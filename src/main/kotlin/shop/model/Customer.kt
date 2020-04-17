package shop.model

data class Customer(
        val name: String,
        val surname: String,
        val customerId: CustomerID
)

data class CustomerID(val id: String)
