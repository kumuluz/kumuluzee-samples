/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.samples.openapi.v2;

import com.kumuluz.ee.samples.openapi.Customer;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerResource class with openAPI
 *
 * @author Zvone Gazvoda
 * @since 3.2.0
 */
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @GET
    @Operation(summary = "Get customers list", description = "Returns a list of customers.")
    @APIResponses({
            @APIResponse(description = "List of customers", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    Customer.class, type = SchemaType.ARRAY)))
    })
    public Response getCustomers() {
        List<Customer> customers = new ArrayList<>();
        Customer c = new Customer("1", "John", "Doe");
        customers.add(c);
        return Response.status(Response.Status.OK).entity(customers).build();
    }

    @GET
    @Operation(summary = "Get customers details", description = "Returns customer details.")
    @APIResponses({
            @APIResponse(description = "Customer details", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    Customer.class)))
    })
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {
        Customer c = new Customer("1", "John", "Doe");
        return Response.status(Response.Status.OK).entity(c).build();
    }

}
