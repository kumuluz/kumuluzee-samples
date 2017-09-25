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
package com.kumuluz.ee.samples.configsources;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Urban Malc
 * @author Jan Meznarič
 * @since 2.5.0
 */
public class ExampleConfigSourceProvider implements ConfigSourceProvider {

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {

        List<ConfigSource> configSources = new LinkedList<>();

        for (int i = 150; i < 160; i++) {
            int finalI = i;
            configSources.add(new ConfigSource() {
                @Override
                public Map<String, String> getProperties() {
                    return null;
                }

                @Override
                public String getValue(String s) {
                    if ("mp.custom-source-ordinal".equals(s)) {
                        return "Hello from custom ConfigSource, generated from ConfigSourceProvider." +
                                " Ordinal: " + finalI;
                    }

                    return null;
                }

                @Override
                public String getName() {
                    return "ExampleConfigSourceFromProvider_" + finalI;
                }

                @Override
                public int getOrdinal() {
                    return finalI;
                }
            });
        }

        return configSources;
    }
}
