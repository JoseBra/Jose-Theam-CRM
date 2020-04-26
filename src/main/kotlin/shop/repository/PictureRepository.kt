package shop.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import shop.model.Picture
import shop.model.PictureID

@Repository
interface PictureRepository : CrudRepository<Picture, PictureID>
