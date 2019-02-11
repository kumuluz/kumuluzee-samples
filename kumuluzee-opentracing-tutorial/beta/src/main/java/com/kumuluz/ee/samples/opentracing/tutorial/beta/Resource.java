package com.kumuluz.ee.samples.opentracing.tutorial.beta;

import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ThreadLocalRandom;

@Path("beta")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Resource {
    private Client client = ClientTracingRegistrar.configure(ClientBuilder.newBuilder()).build();

    @GET
    public Response get() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, 1000 + 1));
            Response r1 = client
                    .target("http://localhost:8081/v1") //alpha
                    .path("alpha")
                    .path("beta")
                    .request()
                    .get();
            String response = r1.readEntity(String.class);
            return Response.ok("beta->" + response).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}
