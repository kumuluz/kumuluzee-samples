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

package com.kumuluz.ee.samples.jcache.rest.listeners;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.samples.jcache.rest.lib.InvoiceData;

import javax.cache.event.*;
import java.io.Serializable;

/**
 * @author cen1
 * @since 3.6.0
 */
public class MyCacheListener implements
        CacheEntryCreatedListener<String, InvoiceData>,
        CacheEntryRemovedListener<String, InvoiceData>,
        CacheEntryExpiredListener<String, InvoiceData>,
        Serializable {

    private static final Logger LOG = LogManager.getLogger(MyCacheListener.class.getName());

    @Override
    public void onCreated(Iterable<CacheEntryEvent<? extends String, ? extends InvoiceData>> iterable) throws CacheEntryListenerException {
        for (CacheEntryEvent<? extends String, ? extends InvoiceData> cacheEntryEvent : iterable) {
            LOG.info("Cache {} stored key {}",
                    cacheEntryEvent.getSource().getName(),
                    cacheEntryEvent.getKey());
        }
    }

    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends String, ? extends InvoiceData>> iterable) throws CacheEntryListenerException {
        for (CacheEntryEvent<? extends String, ? extends InvoiceData> cacheEntryEvent : iterable) {
            LOG.info("Cache {} removed key {}",
                    cacheEntryEvent.getSource().getName(),
                    cacheEntryEvent.getKey());
        }
    }

    @Override
    public void onExpired(Iterable<CacheEntryEvent<? extends String, ? extends InvoiceData>> iterable) throws CacheEntryListenerException {
        for (CacheEntryEvent<? extends String, ? extends InvoiceData> cacheEntryEvent : iterable) {
            LOG.info("Cache {} expired key {}",
                    cacheEntryEvent.getSource().getName(),
                    cacheEntryEvent.getKey());
        }
    }
}