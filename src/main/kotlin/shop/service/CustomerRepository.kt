package shop.service

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import shop.model.Customer

@Repository
interface CustomerRepository : CrudRepository<Customer, Long>
