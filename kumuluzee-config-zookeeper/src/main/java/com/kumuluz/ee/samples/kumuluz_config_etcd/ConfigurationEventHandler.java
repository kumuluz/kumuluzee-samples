/*
 *  Copyright (c) 2014-2020 Kumuluz and/or its affiliates
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

package com.kumuluz.ee.samples.kumuluz_config_etcd;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import java.util.logging.Logger;

/**
 * @author Jan Meznarič
 * @since 2.3.0
 */
@ApplicationScoped
public class ConfigurationEventHandler {

    private static final Logger log = Logger.getLogger(ConfigurationEventHandler.class.getName());

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

        String watchedKey = "rest-config.maintenance";

        ConfigurationUtil.getInstance().subscribe(watchedKey, (String key, String value) -> {

            if (watchedKey.equals(key)) {

                if ("true".equals(value.toLowerCase())) {
                    log.info("Maintenance mode enabled.");
                } else {
                    log.info("Maintenance mode disabled.");
                }

            }

        });
    }

}
