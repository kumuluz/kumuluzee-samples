package com.kumuluz.ee.samples.jms;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Dejan Ognjenović
 * @since 2.4.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
public class CustomerResource {

    @POST
    public Response addCustomerToQueue(Customer customer) {
        QueueHandler.addToQueue(customer);
        return Response.noContent().build();
    }

    @GET
    public Response readCustomerFromQueue() {
        Customer customer = QueueHandler.readFromQueue();
        return customer != null
                ? Response.ok(customer).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

}

