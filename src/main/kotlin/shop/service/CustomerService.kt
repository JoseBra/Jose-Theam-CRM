package shop.service

import arrow.core.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import shop.model.Customer
import shop.model.CustomerID
import shop.repository.CustomerRepository
import shop.utils.CustomerNotFoundException
import shop.utils.IdGenerator

@Service
class CustomerService(
        @Autowired
        val customerRepository: CustomerRepository,
        @Autowired
        val idGenerator: IdGenerator
) {

    fun createCustomer(name: String, surname: String): Customer {
        return customerRepository.save(Customer(CustomerID(idGenerator.generate()), name, surname))
    }

    fun listAllCustomers(): List<Customer> {
        return customerRepository.findAll().toList()
    }

    fun retrieveDetails(customerId: CustomerID): Either<CustomerNotFoundException, Customer> {
        val foundCustomer = customerRepository.findById(customerId)
        return if (foundCustomer.isPresent) {
            Either.right(foundCustomer.get())
        } else {
            Either.left(CustomerNotFoundException("Customer with id ${customerId.id} not found."))
        }
    }
}