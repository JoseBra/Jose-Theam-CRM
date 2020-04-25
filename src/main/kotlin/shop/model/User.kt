package shop.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "Users") //User is a reserved word in PSQL :(
data class User(
        @EmbeddedId
        val userId: UserID,
        val username: String,
        val password: String,

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "users_roles", joinColumns = [JoinColumn(name = "user_id")])
        @Enumerated(EnumType.STRING)
        val roles: List<Role>,
        val isActive: Boolean = true
)

@Embeddable
data class UserID(val id: String) : Serializable
