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
