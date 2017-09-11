package com.kumuluz.ee.samples.cors;

import com.kumuluz.ee.cors.annotations.CrossOrigin;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Created by zvoneg on 01/08/17.
 */
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(allowOrigin = "http://resource-origin.com")
public class CustomerResource {

    @GET
    public Response getCustomers() {

        List<Customer> customers = new ArrayList<>();
        Customer c = new Customer("1", "John", "Doe");

        customers.add(c);

        return Response.status(Response.Status.OK).entity(customers).build();
    }

}
