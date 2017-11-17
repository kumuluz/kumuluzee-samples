package com.kumuluz.ee.samples.tutorial.customers.api.v1.resources;


import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.samples.tutorial.customers.Customer;
import com.kumuluz.ee.samples.tutorial.customers.cdi.CustomersBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Api(value = "Customers", description = "Resource for managing customer date.")
@RequestScoped
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Log
public class CustomersResource {

    @Inject
    private CustomersBean customersBean;

    @Context
    protected UriInfo uriInfo;


    @GET
    @ApiOperation(value = "Get customers", notes = "Returns a list of all customers.", response = Customer.class)
    //@RolesAllowed("user")
    public Response getCustomers() {

        List<Customer> customers = customersBean.getCustomers();

        return Response.ok(customers).build();
    }

    @GET
    @Path("/filtered")
    public Response getCustomersFiltered() {

        List<Customer> customers;

        customers = customersBean.getCustomersFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(customers).build();
    }

    @GET
    @ApiOperation(value = "Get a customer", notes = "Returns a customer with provided id.", response = Customer.class)
    @Path("/{customerId}")
    //@RolesAllowed("user")
    public Response getCustomer(@PathParam("customerId") String customerId) {

        Customer customer = customersBean.getCustomer(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(customer).build();
    }

    @POST
    public Response createCustomer(Customer customer) {

        if ((customer.getFirstName() == null || customer.getFirstName().isEmpty()) || (customer.getLastName() == null
                || customer.getLastName().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            customer = customersBean.createCustomer(customer);
        }

        if (customer.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(customer).build();
        }
    }

    @PUT
    @Path("{customerId}")
    public Response putZavarovanec(@PathParam("customerId") String customerId, Customer customer) {

        customer = customersBean.putCustomer(customerId, customer);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (customer.getId() != null)
                return Response.status(Response.Status.OK).entity(customer).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{customerId}")
    //@RolesAllowed("admin")
    @Metered(name = "delete-requests")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {

        boolean deleted = customersBean.deleteCustomer(customerId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
