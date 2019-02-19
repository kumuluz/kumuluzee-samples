package com.kumuluz.ee.samples.opentracing.tutorial.master;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("master")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Resource {
    private Client client = ClientBuilder.newClient();

    @GET
    public Response get() {
        Response r1 = client
                .target("http://localhost:8081/v1") //alpha
                .path("alpha")
                .request()
                .get();
        Response r2 = client
                .target("http://localhost:8082/v1") //beta
                .path("beta")
                .request()
                .get();
        String response1 = r1.readEntity(String.class);
        String response2 = r2.readEntity(String.class);
        return Response.ok("master->" + response1 + " --- " + "master->" + response2).build();
    }
}
