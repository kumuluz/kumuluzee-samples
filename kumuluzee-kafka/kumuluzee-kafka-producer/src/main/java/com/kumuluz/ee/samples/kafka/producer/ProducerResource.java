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

package com.kumuluz.ee.samples.kafka.producer;

import com.kumuluz.ee.kafka.annotations.KafkaProducer;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Matija Kljun
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/produce")
@RequestScoped
public class ProducerResource {

    private static final Logger log = LogManager.getLogger(ProducerResource.class.getName());

    @Inject
    @KafkaProducer
    private Producer producer;

    @POST
    public Response produceMessage(Message msg){

        ProducerRecord<String,String> record = new ProducerRecord<String,String>( msg.getTopic(), msg.getKey(), msg.getContent());

        producer.send(record,
                (metadata, e) -> {
                    if(e != null) {
                        e.printStackTrace();
                    } else {
                        log.info("The offset of the produced message record is: " + metadata.offset());
                    }
                });

        return Response.ok().build();

    }
}
