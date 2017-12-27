package com.kumuluz.ee.samples.tutorial.customers.api.v1.resources;

import com.kumuluz.ee.samples.tutorial.customers.cdi.CustomersBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zvoneg on 14/10/2017.
 */
@Path("load")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoadResource {

    @Inject
    private CustomersBean customersBean;

    @POST
    public Response loadOrder(Integer n) {

        customersBean.loadOrder(n);

        return Response.status(Response.Status.OK).build();
    }
}
