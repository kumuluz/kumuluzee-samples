package com.kumuluz.ee.samples.opentracing.tutorial.alpha;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("alpha")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Resource {
    private Client client = ClientBuilder.newClient();

    @GET
    public Response get() {
        return Response.ok("alpha").build();
    }

    @GET
    @Path("beta")
    public Response get2() {
        Response r = client
                .target("http://localhost:8083/v1")
                .path("gamma")
                .request()
                .get();
        String response = r.readEntity(String.class);
        return Response.ok("alpha->" + response).build();
    }
}
