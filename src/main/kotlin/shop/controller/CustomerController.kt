package shop.controller

import arrow.core.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import shop.model.Customer
import shop.model.CustomerID
import shop.service.CustomerService
import java.security.Principal

@RestController
class CustomerController {

    @Autowired
    private lateinit var customerService: CustomerService

    @PostMapping(
            "/customers",
            consumes = ["application/json"],
            produces = ["application/json"])
    @PreAuthorize("hasRole('USER')")
    fun createCustomer(
            @RequestBody request: CreateCustomerRequest,
            requestingUserPrincipal: Principal
    ): ResponseEntity<CustomerResponse> {
        val createCustomerAttempt = customerService.createCustomer(request.name, request.surname, requestingUserPrincipal.name)

        when (createCustomerAttempt) {
            is Either.Right ->
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(CustomerResponse.fromCustomer(createCustomerAttempt.b))

            is Either.Left ->
                throw createCustomerAttempt.a
        }
    }

    @PutMapping(
            "/customers/{id}",
            consumes = ["application/json"],
            produces = ["application/json"])
    @PreAuthorize("hasRole('USER')")
    fun updateCustomer(
            @PathVariable id: String,
            @RequestBody request: CreateCustomerRequest,
            requestingUserPrincipal: Principal
    ): ResponseEntity<CustomerResponse> {

        val updateCustomerAttempt = customerService.updateCustomer(
                CustomerID(id),
                request.name,
                request.surname,
                requestingUserPrincipal.name
        )

        when (updateCustomerAttempt) {
            is Either.Right ->
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(CustomerResponse.fromCustomer(updateCustomerAttempt.b))

            is Either.Left ->
                throw updateCustomerAttempt.a
        }
    }

    @GetMapping(
            "/customers",
            produces = ["application/json"])
    @PreAuthorize("hasRole('USER')")
    fun listCustomers(): ResponseEntity<ListCustomerResponse> {
        val allCustomerResponse = customerService.listAllCustomers().map { CustomerResponse.fromCustomer(it) }

        return ResponseEntity
                .ok()
                .body(ListCustomerResponse(allCustomerResponse))
    }

    @GetMapping(
            "/customers/{id}",
            produces = ["application/json"])
    @PreAuthorize("hasRole('USER')")
    fun retrieveDetails(
            @PathVariable id: String
    ): ResponseEntity<CustomerResponse> {
        val findCustomerAttempt = customerService.retrieveDetails(CustomerID(id))

        when (findCustomerAttempt) {
            is Either.Right ->
                return ResponseEntity.ok().body(CustomerResponse.fromCustomer(findCustomerAttempt.b))
            is Either.Left ->
                throw findCustomerAttempt.a
        }
    }

}

data class CreateCustomerRequest(
        val name: String,
        val surname: String
)

data class CustomerResponse(
        val name: String,
        val surname: String,
        val customerId: String,
        val createdBy: UserResponse,
        val lastUpdatedBy: UserResponse?
) {
    companion object {
        fun fromCustomer(customer: Customer) =
                CustomerResponse(
                        customer.name,
                        customer.surname,
                        customer.customerId.id,
                        UserResponse.fromUser(customer.createdBy),
                        customer.lastUpdatedBy?.let { UserResponse.fromUser(it) }
                )
    }
}

data class ListCustomerResponse(
        val items: List<CustomerResponse>
)