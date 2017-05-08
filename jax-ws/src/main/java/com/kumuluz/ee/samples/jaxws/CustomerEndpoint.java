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
package com.kumuluz.ee.samples.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.ResponseWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Benjamin Kastelic
 * @since 2.3.0
 */
@WebService(
        targetNamespace = "http://kumuluz.com/samples/jax-ws/customers/v1",
        serviceName = "CustomerEndpoint",
        portName = "CustomerEndpointSOAP"
)
public class CustomerEndpoint {

    @WebMethod(operationName = "GetCustomers", action = "http://kumuluz.com/samples/jax-ws/customers/v1/GetCustomers")
    @WebResult(name = "output")
    @ResponseWrapper(
            localName = "GetCustomersResponse",
            targetNamespace = "http://kumuluz.com/samples/jax-ws/customers/v1",
            className = "com.kumuluz.ee.samples.jaxws.GetCustomersResponse"
    )
    public GetCustomersResponseMessage getCustomers() {
        Customer customer1 = new Customer();
        customer1.setId("1");
        customer1.setFirstName("John");
        customer1.setLastName("Doe");

        Customer customer2 = new Customer();
        customer2.setId("2");
        customer2.setFirstName("Alice");
        customer2.setLastName("Cooper");

        Customer customer3 = new Customer();
        customer3.setId("3");
        customer3.setFirstName("Bob");
        customer3.setLastName("Builder");

        List<Customer> customers = new ArrayList<>();
        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);

        CustomersList customersList = new CustomersList();
        customersList.setCustomer(customers);

        GetCustomersResponseMessage message = new GetCustomersResponseMessage();
        message.setCustomers(customersList);

        return message;
    }
}
