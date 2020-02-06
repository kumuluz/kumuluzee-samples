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

package com.kumuluz.ee.samples.jcache.rest.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.samples.jcache.rest.lib.InvoiceData;
import com.kumuluz.ee.samples.jcache.rest.producers.DefaultCache;
import com.kumuluz.ee.samples.jcache.rest.producers.MyCache;
import com.kumuluz.ee.samples.jcache.rest.services.InvoiceService;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cen1
 * @since 3.6.0
 */
@ApplicationScoped
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger LOG = LogManager.getLogger(InvoiceServiceImpl.class.getName());

    @DefaultCache
    @Inject
    private Cache<String, InvoiceData> defaultCache;

    @MyCache
    @Inject
    private Cache<String, InvoiceData> myCache;

    private ConcurrentHashMap<String, InvoiceData> db;

    @PostConstruct
    void init() {
        db = new ConcurrentHashMap<>();
        db.put("654321", new InvoiceData("654321"));
        db.put("654322", new InvoiceData("654322"));
        db.put("654323", new InvoiceData("654323"));
        db.put("654324", new InvoiceData("654324"));
        db.put("654325", new InvoiceData("654325"));
    }

    @CachePut(cacheName = "invoices")
    @Override
    public InvoiceData putInvoice(@CacheKey String key, @CacheValue InvoiceData data) {
        LOG.info("putInvoice() put to cache");
        db.put(key, data);
        return data;
    }

    @CacheResult(cacheName = "invoices")
    public InvoiceData getInvoice(@CacheKey String key) {

        LOG.info("getInvoice() Returning non-cached data");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (db.containsKey(key)) {
            return db.get(key);
        }
        else {
            throw new NotFoundException();
        }
    }

    @Override
    public InvoiceData getInvoiceDefault(String key) {

        if (defaultCache.containsKey(key)) {
            return defaultCache.get(key);
        }
        else {
            LOG.info("getInvoiceDefault() Returning non-cached data");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (db.containsKey(key)) {
                defaultCache.put(key, db.get(key));
                return db.get(key);
            }
            else {
                throw new NotFoundException();
            }
        }
    }

    @Override
    public InvoiceData getInvoiceMy(String key) {

        if (myCache.containsKey(key)) {
            return myCache.get(key);
        }
        else {
            LOG.info("getInvoiceMy() Returning non-cached data");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (db.containsKey(key)) {
                myCache.put(key, db.get(key));
                return db.get(key);
            }
            else {
                throw new NotFoundException();
            }
        }
    }
}
