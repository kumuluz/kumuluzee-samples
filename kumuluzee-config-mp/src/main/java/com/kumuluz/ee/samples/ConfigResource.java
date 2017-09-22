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
package com.kumuluz.ee.samples;

import com.kumuluz.ee.samples.models.Customer;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * @author Urban Malc
 * @author Jan Meznarič
 * @since 2.5.0
 */
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Path("config")
public class ConfigResource {

    @Inject
    private Config injectedConfig;

    @Inject
    @ConfigProperty(name = "mp.example-string")
    private String injectedString;

    @Inject
    @ConfigProperty(name = "mp.non-existent-string", defaultValue = "Property does not exist!")
    private String nonExistentString;

    @Inject
    @ConfigProperty(name = "mp.non-existent-string")
    private Optional<String> nonExistentStringOpt;

    @Inject
    @ConfigProperty(name = "mp.example-customer")
    private Customer customer;

    @GET
    public Response testConfig() {

        Config config = ConfigProvider.getConfig();

        String exampleString = config.getValue("mp.example-string", String.class);
        Boolean exampleBoolean = injectedConfig.getValue("mp.example-boolean", boolean.class);

        String response =
                "{" +
                        "\"exampleString\": \"%s\"," +
                        "\"exampleBoolean\": %b," +
                        "\"injectedString\": \"%s\"," +
                        "\"nonExistentString\": \"%s\"," +
                        "\"nonExistentStringOpt\": \"%s\"," +
                        "\"customer\": \"%s\"" +
                        "}";

        response = String.format(
                response,
                exampleString,
                exampleBoolean,
                injectedString,
                nonExistentString,
                nonExistentStringOpt.orElse("Empty Optional"),
                customer
        );

        return Response.ok(response).build();
    }
}
