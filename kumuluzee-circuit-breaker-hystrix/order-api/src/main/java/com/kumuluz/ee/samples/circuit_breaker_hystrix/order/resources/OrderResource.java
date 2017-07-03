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
package com.kumuluz.ee.samples.circuit_breaker_hystrix.order.resources;

import com.kumuluz.ee.samples.circuit_breaker_hystrix.database.Database;
import com.kumuluz.ee.samples.circuit_breaker_hystrix.models.Order;

import javax.annotation.security.PermitAll;
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
@Path("orders")
public class OrderResource {

    @GET
    @PermitAll
    public Response getAllOrders(@QueryParam("customerId") String customerId) {

        List<Order> orders = customerId == null ?
                Database.getOrders() :
                Database.findOrdersByCustomer(customerId);

        return Response.ok(orders).build();
    }

    @GET
    @Path("{orderId}")
    public Response getOrder(@PathParam("orderId") String orderId) {

        Order order = Database.getOrder(orderId);

        return order != null
                ? Response.ok(order).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addOrder(Order order) {

        Database.addOrder(order);

        return Response.noContent().build();
    }

    @DELETE
    @Path("{orderId}")
    public Response removeOrder(@PathParam("orderId") String orderId) {

        Database.deleteOrder(orderId);

        return Response.noContent().build();
    }
}
