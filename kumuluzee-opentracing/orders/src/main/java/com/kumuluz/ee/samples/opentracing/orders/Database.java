/*
 * Copyright (c) 2018 Sunesis, Ltd. and/or its affiliates
 * and other contributors as indicated by the @author tags and
 * the contributor list.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kumuluz.ee.samples.opentracing.orders;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Domen Jeric
 */
public class Database {
    private static List<Order> orders = new ArrayList<Order>();

    public static List<Order> getOrders() {
        return orders;
    }

    public static Order getOrder(String orderId) {
        for (Order order : orders) {
            if (order.getId().equals(orderId))
                return order;
        }

        return null;
    }

    public static void addOrder(Order order) {
        orders.add(order);
    }

    public static void deleteOrder(String orderId) {
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                orders.remove(order);
                break;
            }
        }
    }
}
