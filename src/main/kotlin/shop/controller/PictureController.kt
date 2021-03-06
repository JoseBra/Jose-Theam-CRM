package shop.controller

import arrow.core.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import shop.model.CustomerID
import shop.model.Picture
import shop.service.PictureService

@RestController
class PictureController {

    @Autowired
    private lateinit var pictureService: PictureService

    @PostMapping(
            "/pictures",
            consumes = ["application/json"],
            produces = ["application/json"])
    @PreAuthorize("hasRole('USER')")
    fun uploadPicture(
            @RequestBody request: UploadPictureRequest
    ): ResponseEntity<PictureResponse> {
        val uploadedPictureAttempt = pictureService.uploadPicture(request.imageBase64)

        when (uploadedPictureAttempt) {
            is Either.Right ->
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(PictureResponse.fromPicture(uploadedPictureAttempt.b))
            is Either.Left ->
                throw uploadedPictureAttempt.a
        }
    }

    @GetMapping(
            "/customers/{customerId}/picture",
            consumes = ["application/json"],
            produces = ["application/json"])
    @PreAuthorize("hasRole('USER')")
    fun retrieveCustomerPicture(
            @PathVariable customerId: String
    ): ResponseEntity<PictureResponse> {
        val retrievePictureAttempt = pictureService.retrieveCustomerPicture(CustomerID(customerId))

        when (retrievePictureAttempt) {
            is Either.Right ->
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(PictureResponse.fromPicture(retrievePictureAttempt.b))
            is Either.Left ->
                throw retrievePictureAttempt.a
        }
    }
}

data class UploadPictureRequest(
        val imageBase64: String
)

data class PictureResponse(
        val pictureId: String,
        val imageBase64: String
) {
    companion object {
        fun fromPicture(picture: Picture): PictureResponse {
            return PictureResponse(picture.pictureId.id, picture.imageBase64)
        }
    }
}