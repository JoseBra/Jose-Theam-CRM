package shop.model

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class Picture(
        @EmbeddedId
        val pictureId: PictureID,
        val imageBase64: String
)

@Embeddable
data class PictureID(val id: String) : Serializable
