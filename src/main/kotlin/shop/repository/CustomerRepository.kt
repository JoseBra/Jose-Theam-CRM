package shop.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import shop.model.Customer
import shop.model.CustomerID

@Repository
interface CustomerRepository : CrudRepository<Customer, CustomerID>
