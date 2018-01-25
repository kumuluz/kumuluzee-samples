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
package com.kumuluz.ee.samples.discovery.discover.jaxrs;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * @author Urban Malc
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("discover")
@RequestScoped
public class DiscoverResource {

    @Inject
    @DiscoverService(value = "customer-service", version = "1.0.x", environment = "dev")
    private Optional<WebTarget> target;

    @GET
    @Path("url")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUrl() {
        if (target.isPresent()) {
            return Response.ok(target.get().getUri().toString()).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    public Response getProxiedCustomers() {
        if (target.isPresent()) {
            WebTarget service = target.get().path("v1/customers");

            Response response;
            try {
                response = service.request().get();
            } catch (ProcessingException e) {
                return Response.status(408).build();
            }

            ProxiedResponse proxiedResponse = new ProxiedResponse();
            proxiedResponse.setResponse(response.readEntity(String.class));
            proxiedResponse.setProxiedFrom(target.get().getUri().toString());

            return Response.ok(proxiedResponse).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        if (target.isPresent()) {
            WebTarget service = target.get().path("v1/customers");

            Response response;
            try {
                response = service.request().post(Entity.json(customer));
            } catch (ProcessingException e) {
                return Response.status(408).build();
            }

            return Response.fromResponse(response).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }
}
