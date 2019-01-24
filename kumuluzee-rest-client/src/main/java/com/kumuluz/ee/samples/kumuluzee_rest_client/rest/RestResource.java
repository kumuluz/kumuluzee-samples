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
package com.kumuluz.ee.samples.kumuluzee_rest_client.rest;

import com.kumuluz.ee.samples.kumuluzee_rest_client.api.CustomerApi;
import com.kumuluz.ee.samples.kumuluzee_rest_client.api.SensitiveDataResponseMapper;
import com.kumuluz.ee.samples.kumuluzee_rest_client.entities.Customer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * @author Urban Malc
 * @since 3.1.0
 */
@RequestScoped
@Path("operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestResource {

    @Inject
    @RestClient
    private CustomerApi customerApi;

    @GET
    public Response getAllCustomers() {
        return Response.ok(customerApi.getAllCustomers()).build();
    }

    @GET
    @Path("batch")
    public Response createBatchCustomers() {
        String[] ids = {"1", "2", "3"};
        String[] firstNames = {"Jonh", "Mary", "Joe"};
        String[] lastNames = {"Doe", "McCallister", "Green"};

        for (int i = 0; i < ids.length; i++) {
            Customer c = new Customer();
            c.setId(ids[i]);
            c.setFirstName(firstNames[i]);
            c.setLastName(lastNames[i]);

            customerApi.createCustomer(c);
        }

        return Response.noContent().build();
    }

    @GET
    @Path("batchAsynch")
    public Response createBatchCustomersAsynch() {
        String[] ids = {"1", "2", "3"};
        String[] firstNames = {"Jonh", "Mary", "Joe"};
        String[] lastNames = {"Doe", "McCallister", "Green"};

        List<CompletionStage<Void>> requests = new LinkedList<>();

        for (int i = 0; i < ids.length; i++) {
            Customer c = new Customer();
            c.setId(ids[i]);
            c.setFirstName(firstNames[i]);
            c.setLastName(lastNames[i]);

            requests.add(customerApi.createCustomerAsynch(c));
        }

        boolean hasError = false;

        for (CompletionStage<Void> cs : requests) {
            try {
                cs.toCompletableFuture().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                hasError = true;
            }
        }

        if (hasError) {
            return Response.serverError().build();
        }

        return Response.noContent().build();
    }

    @GET
    @Path("{cId}")
    public Response getSingleMasked(@PathParam("cId") String customerId) throws MalformedURLException {

        CustomerApi programmaticLookupApi = RestClientBuilder.newBuilder()
                .baseUrl(new URL("http://localhost:8080/v1"))
                .register(SensitiveDataResponseMapper.class)
                .build(CustomerApi.class);

        Customer c = programmaticLookupApi.getCustomer(customerId);
        c.setFirstName(c.getFirstName().substring(0, 1) + getStars(c.getFirstName().length() - 1));
        c.setLastName(c.getLastName().substring(0, 1) + getStars(c.getLastName().length() - 1));

        return Response.ok(c).build();
    }

    private String getStars(int len) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < len; i++) {
            s.append("*");
        }

        return s.toString();
    }
}
