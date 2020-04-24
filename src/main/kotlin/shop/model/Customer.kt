package shop.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "Customer")
data class Customer(
        @EmbeddedId
        val customerId: CustomerID,
        val name: String,
        val surname: String,
        @ManyToOne(fetch = FetchType.LAZY)
        val createdBy: User
)

@Embeddable
data class CustomerID(val id: String): Serializable
