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
package com.kumuluz.ee.samples.circuit_breaker_hystrix.customer.resources;

import com.kumuluz.ee.samples.circuit_breaker_hystrix.customer.beans.OrdersBean;
import com.kumuluz.ee.samples.circuit_breaker_hystrix.database.Database;
import com.kumuluz.ee.samples.circuit_breaker_hystrix.models.Customer;
import com.kumuluz.ee.samples.circuit_breaker_hystrix.models.Order;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Luka Šarc
 * @since 2.3.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
@Path("customers")
public class CustomerResource {

    @Inject
    private OrdersBean ordersBean;

    @GET
    @PermitAll
    public Response getAllCustomers() {

        List<Customer> customers = Database.getCustomers();

        return Response.ok(customers).build();
    }

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {

        Customer customer = Database.getCustomer(customerId);

        if (customer == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        List<Order> customerOrders = ordersBean.findOrdersByCustomerId(customerId);
        customer.setOrders(customerOrders);

        return Response.ok(customer).build();
    }

    @POST
    public Response addCustomer(Customer customer) {

        Database.addCustomer(customer);

        return Response.noContent().build();
    }

    @DELETE
    @Path("{customerId}")
    public Response removeCustomer(@PathParam("customerId") String customerId) {

        Database.deleteCustomer(customerId);

        return Response.noContent().build();
    }

    @GET
    @Path("{customerId}/order")
    public Response getCustomerOrders(@PathParam("customerId") String customerId) {

        List<Order> customerOrders = ordersBean.findOrdersByCustomerId(customerId);

        return Response.ok(customerOrders).build();
    }
}
