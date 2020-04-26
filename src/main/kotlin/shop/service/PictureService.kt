package shop.service

import arrow.core.Either
import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import shop.model.Picture
import shop.model.PictureID
import shop.repository.PictureRepository
import shop.utils.IdGenerator
import shop.utils.InvalidBase64Picture

@Service
class PictureService(
        @Autowired
        val pictureRepository: PictureRepository,
        @Autowired
        val idGenerator: IdGenerator
) {
    fun uploadPicture(imageBase64: String): Either<InvalidBase64Picture, Picture> {
        return if (isImageEncoded(imageBase64)) {
            val pictureToSave = Picture(
                    PictureID(idGenerator.generate()),
                    imageBase64
            )

            Either.right(pictureRepository.save(pictureToSave))
        } else {
            Either.left(InvalidBase64Picture("Invalid Base64 String."))
        }
    }

    private fun isImageEncoded(imageBase64: String): Boolean {
        val splitMimeBase64 = imageBase64.split(",")
        return splitMimeBase64.first().contains("data:image/") && Base64.isBase64(splitMimeBase64.last())
    }
}
