/*
 *  Copyright (c) 2014-2019 Kumuluz and/or its affiliates
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
package com.kumuluz.ee.samples.api;

import com.kumuluz.ee.feature.flags.common.utils.FeatureFlags;
import com.kumuluz.ee.feature.flags.unleash.utils.UnleashFeatureFlags;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Blaž Mrak
 * @since 1.0.0
 */
@Path("/features")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class RestResource {

    @Inject
    private FeatureFlags featureFlags;

//    @Inject
//    private UnleashFeatureFlags featureFlags;

    @GET
    public Map<String, String> featureEnabled() {
        Map<String, String> result = new HashMap();

        if (featureFlags.isEnabled("test-feature")) {
            result.put("message", "test-feature is enabled");
        } else {
            result.put("message", "test-feature is disabled");
        }

        return result;
    }
}
