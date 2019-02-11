package com.kumuluz.ee.samples.opentracing.tutorial.beta;

import io.opentracing.Tracer;
import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
@RequestScoped
public class Resource {
    private Client client = ClientTracingRegistrar.configure(ClientBuilder.newBuilder()).build();

    @Inject
    private Tracer tracer;

    @GET
    public Response get() {
        try {
            int waitDelay = ThreadLocalRandom.current().nextInt(1, 1000 + 1);
            tracer.activeSpan().setTag("waitDelay", waitDelay);
            tracer.activeSpan().log("Waited " + waitDelay + " milliseconds.");
            tracer.activeSpan().setBaggageItem("waitDelay", String.valueOf(waitDelay));
            Thread.sleep(waitDelay);
            Response r1 = client
                    .target("http://localhost:8081/v1")
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
