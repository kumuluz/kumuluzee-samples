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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.logging.Logger;

/**
 *
 * @author Žan Ožbot
 */
public class DiscoveryVerticle extends AbstractVerticle {

	private static final Logger log = Logger.getLogger(DiscoveryVerticle.class.getName());

    private Record record;
    private ServiceDiscovery discovery;

    private final JsonObject SERVICE = new JsonObject()
												.put("name", "customer-service")
												.put("version", "1.0.0")
												.put("env", "dev");

    private final String REQUEST_ADDRESS = "vertx.discovery.request";

	@Override
	public void start() throws Exception {
		createServer();
		publishService();
	}

	private void createServer() {
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		router.get("/").handler(routingContext -> {
			routingContext.response()
					.end((new JsonObject().put("status", "UP")).toString());
		});

		router.get("/discover").handler(routingContext -> {
			getService(routingContext.response());
		});

		server.requestHandler(router::accept).listen(8082);
	}

	private void getService(HttpServerResponse response) {
		vertx.eventBus().send(REQUEST_ADDRESS, SERVICE, ar -> {
			if (ar.succeeded() && ar.result() != null) {
				JsonObject reply = (JsonObject) ar.result().body();
				response.end(reply.toString());
			} else {
				response.end((new JsonObject().put("message", "Failed to retrieve service url.")).toString());
			}
		});
	}

	private void publishService() {
		discovery = ServiceDiscovery.create(vertx);

		record = HttpEndpoint.createRecord("some-rest-api", "localhost", 8080, "");

		discovery.publish(record, ar -> {
			if (ar.succeeded()) {
				record.setRegistration(ar.result().getRegistration());
				log.info("Service was successfully registered.");
			} else {
				log.info("Vert.x service registration failed.");
			}
		});
	}

	@Override
	public void stop() throws Exception {
        discovery.unpublish(record.getRegistration(), ar -> {
            if(ar.succeeded()) {
                log.info("Service was successfully deregistered.");
            } else {
                log.info("Error deregistering service.");
            }
        });
	}

}
