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
package com.kumuluz.ee.samples.rest;

import com.kumuluz.ee.rest.beans.Queried;
import com.kumuluz.ee.rest.beans.QueryParameters;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Benjamin Kastelic, Marko Skrjanec
 * @since 2.3.0
 */
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
public class CustomerResource {

    @Context
    protected UriInfo uriInfo;

    @Inject
    private CustomerService customerBean;

    @GET
    public Response getAllCustomers() {
        Queried<Customer> customers = customerBean.getCustomers(createQuery());

        List<Customer> response = customers.stream()
                //.map() // my api object mapper
                .collect(Collectors.toList());

        return Response.ok(response).header("total", customers.getTotalCount()).build();
    }

    @GET
    @Path("count")
    public Response getCount() {
        Long count = customerBean.getCustomerCount(createQuery());
        return Response.ok(count).build();
    }

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {
        Customer customer = customerBean.getCustomer(customerId);
        return customer != null
                ? Response.ok(customer).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        customerBean.saveCustomer(customer);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {
        customerBean.deleteCustomer(customerId);
        return Response.noContent().build();
    }

    /**
     * Helper method for parsing query parameters from uri.
     *
     * @return query parameters
     */
    private QueryParameters createQuery() {
        return QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0).defaultLimit(10).build();
    }
}
