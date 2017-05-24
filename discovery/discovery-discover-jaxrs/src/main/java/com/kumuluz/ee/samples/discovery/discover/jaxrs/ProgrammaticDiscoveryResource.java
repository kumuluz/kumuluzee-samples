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
package com.kumuluz.ee.samples.discovery.discover.jaxrs;

import com.kumuluz.ee.discovery.utils.DiscoveryUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * @author Urban Malc
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("programmatic")
@RequestScoped
public class ProgrammaticDiscoveryResource {

    @Inject
    DiscoveryUtil discoveryUtil;

    @GET
    @Path("{serviceName}/{serviceVersion}/{environment}")
    public Response getInstances(@PathParam("serviceName") String serviceName,
                                 @PathParam("serviceVersion") String serviceVersion,
                                 @PathParam("environment") String environment) {

        Optional<List<URL>> instances = discoveryUtil.getServiceInstances(serviceName, serviceVersion, environment);

        if (instances.isPresent()) {
            return Response.ok(instances.get()).build();
        } else {
            return Response.noContent().build();
        }
    }
}
