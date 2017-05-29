package producer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/produce")
@RequestScoped
public class ProducerResource {

    @Inject
    private TestProducer producer;

    @POST
    public Response produceMessage(Message msg){

        producer.send(msg.getTopic(), msg.getContent(), msg.getKey());

        return Response.ok().build();

    }
}
