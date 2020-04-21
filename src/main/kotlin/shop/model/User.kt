package shop.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "Users") //User is a reserved word in PSQL :(
data class User(
        @EmbeddedId
        val customerId: UserID,
        val username: String,
        val password: String,
        @ElementCollection
        val roles: List<Role>
)

@Embeddable
data class UserID(val id: String) : Serializable
