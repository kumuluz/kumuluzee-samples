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
package com.kumuluz.ee.samples.circuit_breaker_hystrix.customer.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.fault.tolerance.annotations.*;
import com.kumuluz.ee.samples.circuit_breaker_hystrix.models.Order;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luka Šarc
 * @since 2.3.0
 */
@RequestScoped
@Bulkhead
@GroupKey("orders")
public class OrdersBean {

    private static final Logger log = LoggerFactory.getLogger(OrdersBean.class);

    private static String ordersApiPath;

    @Inject
    private HttpClient httpClient;

    private ObjectMapper objectMapper;

    static {
        ordersApiPath = ConfigurationUtil.getInstance().get("orders-api.path")
                .orElse("http://localhost:8081/v1/order");

        log.info("Orders API path set to " + ordersApiPath);
    }

    public OrdersBean() {
        objectMapper = new ObjectMapper();
    }

    @CircuitBreaker
    @Fallback(fallbackMethod = "findOrdersByCustomerIdFallback")
    @CommandKey("http-find-order")
    @Timeout
    @Asynchronous
    public List<Order> findOrdersByCustomerId(String customerId) {

        try {

            HttpGet request = new HttpGet(ordersApiPath + "?customerId=" + customerId);
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null)
                    return toOrdersArray(EntityUtils.toString(entity));
            } else {
                String msg = "Remote server '" + ordersApiPath + "' failed with status " + status + ".";
                log.warn(msg);
                throw new InternalServerErrorException(msg);
            }

        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            log.warn(msg);
            throw new InternalServerErrorException(msg);
        }

        return new ArrayList<>();
    }

    public List<Order> findOrdersByCustomerIdFallback(String customerId) {

        log.info("Fallback called for findOrdersByCustomerId.");

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setName("N/A");
        order.setPaymentType("N/A");
        order.setId("N/A");

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        return orders;
    }

    private List<Order> toOrdersArray(String json) throws IOException {

        return json == null ?
                new ArrayList<>() :
                objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
    }

}
