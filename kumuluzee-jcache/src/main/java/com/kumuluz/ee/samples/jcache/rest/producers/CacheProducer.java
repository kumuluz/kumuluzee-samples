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

package com.kumuluz.ee.samples.jcache.rest.producers;

import com.kumuluz.ee.samples.jcache.rest.lib.InvoiceData;
import com.kumuluz.ee.samples.jcache.rest.listeners.MyCacheListener;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * @author cen1
 * @since 3.6.0
 */
public class CacheProducer {

    @Inject
    private CacheManager cacheManager;

    @MyCache
    @Produces
    @ApplicationScoped
    public Cache<String, InvoiceData> getMyCache() {
        /**
         * My cache is not configured in config.yml, dynamic configuration
         * For explanation on different expiry policy factories, see javadoc on javax.cache.expiry
         */
        final CacheEntryListenerConfiguration<String, InvoiceData> listenersConfiguration
                = new MutableCacheEntryListenerConfiguration<>(FactoryBuilder.factoryOf(MyCacheListener.class),
                null, false, true);

        final MutableConfiguration<String, InvoiceData> config = new MutableConfiguration<String, InvoiceData>()
                .setTypes(String.class, InvoiceData.class)
                .addCacheEntryListenerConfiguration(listenersConfiguration)
                //.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 5)))
                //.setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 5)))
                //.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 5)))
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 5)));
        return cacheManager.createCache("my", config);
    }

    @DefaultCache
    @Produces
    @ApplicationScoped
    public Cache<String, InvoiceData> getDefaultCache() {
        //Default cache is configured in config.yml
        return cacheManager.getCache("default");
    }
}