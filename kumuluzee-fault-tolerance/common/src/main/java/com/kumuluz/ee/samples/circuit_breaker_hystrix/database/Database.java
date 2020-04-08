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
package com.kumuluz.ee.samples.circuit_breaker_hystrix.database;

import com.kumuluz.ee.samples.circuit_breaker_hystrix.models.Customer;
import com.kumuluz.ee.samples.circuit_breaker_hystrix.models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Luka Šarc
 * @since 2.3.0
 */
public class Database {

    private static List<Customer> customers = new ArrayList<>();
    private static List<Order> orders = new ArrayList<>();

    public static List<Customer> getCustomers() {
        return customers;
    }
    public static List<Order> getOrders() { return orders; }

    public static Customer getCustomer(String customerId) {

        for (Customer customer : customers) {
            if (customer.getId().equals(customerId))
                return customer;
        }

        return null;
    }

    public static void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public static void deleteCustomer(String customerId) {

        for (Customer customer : customers) {
            if (customer.getId().equals(customerId)) {
                customers.remove(customer);
                break;
            }
        }
    }

    public static void addOrder(String customerId) {

        for (Customer customer : customers) {
            if (customer.getId().equals(customerId)) {
                customers.remove(customer);
                break;
            }
        }
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

    public static List<Order> findOrdersByCustomer(String customerId) {

        return orders.stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    static {
        Customer customer1 = new Customer();
        customer1.setId("1");
        customer1.setFirstName("Brian");
        customer1.setLastName("Walker");

        Customer customer2 = new Customer();
        customer2.setId("2");
        customer2.setFirstName("Lisa");
        customer2.setLastName("Smith");

        Customer customer3 = new Customer();
        customer3.setId("3");
        customer3.setFirstName("James");
        customer3.setLastName("Dylan");

        Order order1 = new Order();
        order1.setId("1");
        order1.setPrice(9.90);
        order1.setPaymentType("CREDIT_CARD");
        order1.setName("Tom&Jerry");
        order1.setCustomerId("1");

        Order order2 = new Order();
        order2.setId("2");
        order2.setPrice(13.90);
        order2.setPaymentType("CASH");
        order2.setName("Looney Tunes");
        order2.setCustomerId("1");

        Order order3 = new Order();
        order3.setId("3");
        order3.setPrice(1699.90);
        order3.setPaymentType("CREDIT_CARD");
        order3.setName("Laptop");
        order3.setCustomerId("2");

        Order order4 = new Order();
        order4.setId("4");
        order4.setPrice(49.90);
        order4.setPaymentType("CREDIT_CARD");
        order4.setName("Keyboard");
        order4.setCustomerId("2");

        Order order5 = new Order();
        order5.setId("5");
        order5.setPrice(79.90);
        order5.setPaymentType("CREDIT_CARD");
        order5.setName("Mouse");
        order5.setCustomerId("2");

        Order order6 = new Order();
        order6.setId("6");
        order6.setPrice(99.90);
        order6.setPaymentType("PREPAYMENT");
        order6.setName("Holidays");
        order6.setCustomerId("3");

        addCustomer(customer1);
        addCustomer(customer2);
        addCustomer(customer3);

        addOrder(order1);
        addOrder(order2);
        addOrder(order3);
        addOrder(order4);
        addOrder(order5);
        addOrder(order6);
    }
}
