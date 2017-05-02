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
package com.kumuluz.ee.samples.microservices.simple;

import com.kumuluz.ee.samples.microservices.simple.models.Book;
import com.kumuluz.ee.samples.microservices.simple.models.Order;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
@Path("/orders")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrdersResource {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private OrdersProperties ordersProperties;

    /**
     * <p>Queries the database and returns a specific order based on the given id.</p>
     *
     * @param id The id of the wanted book.
     * @return Response object containing the requested book, or empty with the NOT_FOUND status.
     */
    @GET
    @Path("/{id}")
    public Response getOrder(@PathParam("id") Integer id) {

        Order o = em.find(Order.class, id);

        if (o == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(o).build();
    }

    /**
     * <p>Creates the order for the provided book.</p>
     *
     * @param b The book object for which the order will be placed.
     * @return Response object containing the created order.
     */
    @POST
    public Response placeOrder(Book b) {

        if (b == null || b.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Response bookResponse = ClientBuilder.newClient()
                .target(ordersProperties.getCatalogueUrl()).path("books").path(b.getId().toString()).request().get();

        if (!bookResponse.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Order o = new Order();
        o.setBook(bookResponse.readEntity(Book.class));
        o.setOrderDate(new Date());

        em.getTransaction().begin();

        em.persist(o);

        em.getTransaction().commit();

        return Response.status(Response.Status.CREATED).entity(o).build();
    }
}
