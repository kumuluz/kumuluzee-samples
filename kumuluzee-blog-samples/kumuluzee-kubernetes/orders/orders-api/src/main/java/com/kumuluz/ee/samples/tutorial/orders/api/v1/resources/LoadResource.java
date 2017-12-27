package com.kumuluz.ee.samples.tutorial.orders.api.v1.resources;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zvoneg on 13/10/2017.
 */
@RequestScoped
@Path("/load")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoadResource {

    @POST
    public Response createLoad(Integer n) {

        for (int i = 1; i <= n; i++) {
            fibonacci(i);
        }

        return Response.status(Response.Status.OK).build();
    }

    private long fibonacci(int n) {
        if (n <= 1) return n;
        else return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
