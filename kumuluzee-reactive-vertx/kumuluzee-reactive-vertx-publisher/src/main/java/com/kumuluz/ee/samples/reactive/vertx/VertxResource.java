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
package com.kumuluz.ee.samples.reactive.vertx;

import com.kumuluz.ee.reactive.common.annotations.ReactiveEventPublisher;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
*
* @author Žan Ožbot
*/
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("vertx")
@RequestScoped
public class VertxResource {
	
	@Inject
	@ReactiveEventPublisher(address = "tacos")
	MessageProducer<Object> tacos;
	
	@POST
	@Path("publish")
	public Response sendMessage(Message message) {
		tacos.send(message.getContent());
		
		JsonObject reponse = new JsonObject()
				.put("message", message.getContent())
				.put("status", "sent");
		
		return Response.ok(reponse.encodePrettily()).build();
	}
	
}
