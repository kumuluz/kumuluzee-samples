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
package com.kumuluz.ee.samples.circuit_breaker_hystrix.customer.producers;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Luka Šarc
 * @since 2.3.0
 */
public class HttpClientProducer {

    private static final int DEFAULT_POOL_MAX_CONNECTIONS = 5;

    @Produces
    @ApplicationScoped
    public HttpClient httpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, (certificate, authType) -> true)
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        Registry socketFactorRegistry = RegistryBuilder.create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", socketFactory)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactorRegistry);
        connectionManager.setMaxTotal(DEFAULT_POOL_MAX_CONNECTIONS);

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setConnectionManager(connectionManager)
                .build();
    }

}
