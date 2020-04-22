package shop.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import shop.model.Customer
import shop.service.CustomerService

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
            @RequestBody request: CreateCustomerRequest
    ): ResponseEntity<CustomerResponse> {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustomerResponse.fromCustomer(customerService.createCustomer(request.name, request.surname)))
    }

}

data class CreateCustomerRequest(
        val name: String,
        val surname: String
)

data class CustomerResponse(
        val name: String,
        val surname: String,
        val customerId: String
) {
    companion object {
        fun fromCustomer(customer: Customer) =
                CustomerResponse(customer.name, customer.surname, customer.customerId.id)
    }
}