package com.kumuluz.ee.samples.tutorial.orders.api.v1.resources;

import com.kumuluz.ee.samples.tutorial.orders.api.v1.health.HealthManagementDemo;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/management")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ManagementResource {

    @Inject
    private HealthManagementDemo healthManagementDemo;

    @POST
    @Path("healthy")
    public Response setHealthStatus(Boolean healthy) {

        healthManagementDemo.setHealthy(healthy);

        return Response.ok().build();

    }


}
