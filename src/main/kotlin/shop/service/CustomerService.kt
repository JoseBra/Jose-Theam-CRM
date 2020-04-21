package shop.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import shop.model.Customer
import shop.model.CustomerID
import shop.repository.CustomerRepository
import shop.utils.IdGenerator

@Service
class CustomerService(
        @Autowired
        var customerRepository: CustomerRepository,
        @Autowired
        var idGenerator: IdGenerator
) {

    fun createCustomer(name: String, surname: String): Customer {
        return customerRepository.save(Customer(CustomerID(idGenerator.generate()), name, surname))
    }
}