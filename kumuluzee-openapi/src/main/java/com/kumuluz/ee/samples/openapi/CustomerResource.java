package com.kumuluz.ee.samples.openapi;

import io.swagger.oas.annotations.Operation;
import io.swagger.oas.annotations.media.Content;
import io.swagger.oas.annotations.media.Schema;
import io.swagger.oas.annotations.responses.ApiResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerResource class with openAPI
 *
 * @author Zvone Gazvoda
 * @since 2.5.0
 */
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @GET
    @Operation(summary = "Get customers list", tags = {"customers"}, description = "Returns a list of customers.", responses = {
            @ApiResponse(description = "List of customers", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    Customer.class)))
    })
    public Response getCustomers() {

        List<Customer> customers = new ArrayList<>();
        Customer c = new Customer("1", "John", "Doe");

        customers.add(c);

        return Response.status(Response.Status.OK).entity(customers).build();
    }

}
