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
package com.kumuluz.ee.samples.graphql_simple;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Benjamin Kastelic
 * @since 2.3.0
 */
@RequestScoped
public class CustomerService {

    @PersistenceContext
    private EntityManager em;

    public Customer getCustomer(String customerId) {
        return em.find(Customer.class, customerId);
    }

    public List<Customer> getCustomers() {
        List<Customer> customers = em
                .createNamedQuery("Customer.findCustomers", Customer.class)
                .getResultList();

        return customers;
    }

    @Transactional
    public void saveCustomer(Customer customer) {
        if (customer != null) {
            em.persist(customer);
        }

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteCustomer(String customerId) {
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            em.remove(customer);
        }
    }
}
