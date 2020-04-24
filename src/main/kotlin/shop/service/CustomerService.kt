package shop.service

import arrow.core.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import shop.model.Customer
import shop.model.CustomerID
import shop.repository.CustomerRepository
import shop.repository.UserRepository
import shop.utils.CustomerNotFoundException
import shop.utils.IdGenerator
import shop.utils.UserNotFound

@Service
class CustomerService(
        @Autowired
        val customerRepository: CustomerRepository,
        @Autowired
        val userRepository: UserRepository,
        @Autowired
        val idGenerator: IdGenerator
) {

    fun createCustomer(name: String, surname: String, creatingUsername: String): Either<UserNotFound, Customer> {
        val creatingUser = userRepository.findByUsername(creatingUsername)

        return if (creatingUser != null) {
            Either.right(customerRepository.save(Customer(
                    CustomerID(idGenerator.generate()),
                    name,
                    surname,
                    creatingUser)))
        } else {
            Either.left(UserNotFound("Trying to create a customer with a user that does not exist."))
        }
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