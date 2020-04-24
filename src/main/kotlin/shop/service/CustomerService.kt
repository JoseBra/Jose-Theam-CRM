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
import shop.utils.UserNotFoundException

@Service
class CustomerService(
        @Autowired
        val customerRepository: CustomerRepository,
        @Autowired
        val userRepository: UserRepository,
        @Autowired
        val idGenerator: IdGenerator
) {

    fun createCustomer(name: String, surname: String, creatingUsername: String): Either<UserNotFoundException, Customer> {
        val creatingUser = userRepository.findByUsername(creatingUsername)
                ?: return Either.left(UserNotFoundException("Trying to create a customer with a user that does not exist."))

        return Either.right(customerRepository.save(Customer(
                CustomerID(idGenerator.generate()),
                name,
                surname,
                creatingUser)))
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

    fun updateCustomer(
            customerId: CustomerID,
            newName: String,
            newSurname: String,
            updatingUserUsername: String): Either<Exception, Customer> {

        val updatingUser = userRepository.findByUsername(updatingUserUsername)
                ?: return Either.left(UserNotFoundException("Trying to create a customer with a user that does not exist."))

        val foundCustomer = customerRepository.findById(customerId)

        return if (foundCustomer.isPresent) {
            Either.right(customerRepository.save(foundCustomer.get().copy(
                    name = newName,
                    surname = newSurname,
                    lastUpdatedBy = updatingUser
            )))
        } else {
            Either.left(CustomerNotFoundException("Customer with id ${customerId.id} not found."))
        }
    }

    fun deleteCustomer(customerId: CustomerID): Either<CustomerNotFoundException, Customer> {
        val foundCustomer = customerRepository.findById(customerId)

        return if (foundCustomer.isPresent) {
            customerRepository.delete(foundCustomer.get())
            Either.right(foundCustomer.get())
        } else {
            Either.left(CustomerNotFoundException("Customer with id ${customerId.id} not found."))
        }
    }
}