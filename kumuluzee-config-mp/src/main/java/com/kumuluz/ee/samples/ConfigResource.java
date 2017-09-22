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
@Produces(MediaType.TEXT_PLAIN)
@Path("config")
public class ConfigResource {

    @Inject
    private Config injectedConfig;

    @Inject
    @ConfigProperty(name = "mp.exampleString")
    private String injectedString;

    @Inject
    @ConfigProperty(name = "mp.non-existent-string", defaultValue = "Property does not exist!")
    private String nonExistentString;

    @Inject
    @ConfigProperty(name = "mp.non-existent-string")
    private Optional<String> nonExistentStringOpt;

    @Inject
    @ConfigProperty(name = "mp.exampleCustomer")
    private Customer customer;

    @GET
    public Response testConfig() {
        StringBuilder response = new StringBuilder();

        Config config = ConfigProvider.getConfig();
        response.append(config.getValue("mp.exampleString", String.class)).append('\n');

        response.append(injectedConfig.getValue("mp.exampleBoolean", boolean.class)).append('\n');

        response.append(injectedString).append('\n');
        response.append(nonExistentString).append('\n');

        response.append(nonExistentStringOpt.orElse("Empty Optional")).append('\n');

        response.append(customer).append('\n');

        response.append(config.getValue("mp.customSourceValue", String.class));

        response.append(config.getValue("mp.customSourceOrdinal", String.class));

        return Response.ok(response.toString()).build();
    }
}
