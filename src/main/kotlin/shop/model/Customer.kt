package shop.model

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.io.Serializable
import javax.persistence.*

@Entity
data class Customer(
        @EmbeddedId
        val customerId: CustomerID,
        val name: String,
        val surname: String,
        @ManyToOne(fetch = FetchType.LAZY)
        val createdBy: User,
        @ManyToOne(fetch = FetchType.LAZY, optional = true)
        val lastUpdatedBy: User? = null,
        @OneToOne(fetch = FetchType.LAZY, optional = true)
        @JoinColumn(name = "picture_id")
        @Cascade(CascadeType.DELETE)
        val picture: Picture? = null
)

@Embeddable
data class CustomerID(val id: String) : Serializable
