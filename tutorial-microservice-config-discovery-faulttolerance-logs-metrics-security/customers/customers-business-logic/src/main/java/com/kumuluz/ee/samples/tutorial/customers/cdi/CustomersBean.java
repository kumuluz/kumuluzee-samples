package com.kumuluz.ee.samples.tutorial.customers.cdi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.fault.tolerance.annotations.*;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.kumuluz.ee.samples.tutorial.customers.Customer;
import com.kumuluz.ee.samples.tutorial.customers.cdi.configuration.RestProperties;
import com.kumuluz.ee.samples.tutorial.orders.Order;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequestScoped
@GroupKey("orders")
public class CustomersBean {

    private Logger log = LogManager.getLogger(CustomersBean.class.getName());

    @PersistenceContext(unitName = "customers-jpa")
    private EntityManager em;

    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    @Inject
    @DiscoverService(value = "order-service", environment = "dev", version = "*")
    private Optional<String> basePath;

    @Inject
    private RestProperties restProperties;

    @Inject
    private CustomersBean customersBean;

    @PostConstruct
    private void init() {
        httpClient = HttpClientBuilder.create().build();
        objectMapper = new ObjectMapper();
    }

    public List<Customer> getCustomers() {

        Query query = em.createNamedQuery("Customer.getAll", Customer.class);

        return query.getResultList();

    }

    public List<Customer> getCustomersFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        List<Customer> customers = JPAUtils.queryEntities(em, Customer.class, queryParameters);

        return customers;
    }

    public Customer getCustomer(String customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer == null) {
            throw new NotFoundException();
        }

        if (restProperties.isOrderServiceEnabled()) {
            List<Order> orders = customersBean.getOrders(customerId);
            customer.setOrders(orders);
        }

        return customer;
    }

    public Customer createCustomer(Customer customer) {

        try {
            beginTx();
            em.persist(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public Customer putCustomer(String customerId, Customer customer) {

        Customer c = em.find(Customer.class, customerId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            customer.setId(c.getId());
            customer = em.merge(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public boolean deleteCustomer(String customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer != null) {
            try {
                beginTx();
                em.remove(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    @CircuitBreaker(failureRatio = 0.3)
    @Fallback(fallbackMethod = "getOrdersFallback")
    @CommandKey("http-get-order")
    @Timeout(value = 500)
    public List<Order> getOrders(String customerId) {

        if (basePath.isPresent()) {
            try {
                HttpGet request = new HttpGet(basePath.get() + "/v1/orders?where=customerId:EQ:" + customerId);
                HttpResponse response = httpClient.execute(request);

                int status = response.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();

                    if (entity != null)
                        return getObjects(EntityUtils.toString(entity));
                } else {
                    String msg = "Remote server '" + basePath.get() + "' is responded with status " + status + ".";
                    log.error(msg);
                    throw new InternalServerErrorException(msg);
                }

            } catch (IOException e) {
                String msg = e.getClass().getName() + " occured: " + e.getMessage();
                log.error(msg);
                throw new InternalServerErrorException(msg);
            }
        } else {
            log.error("Orders service not available");
        }
        return new ArrayList<>();
    }

    public List<Order> getOrdersFallback(String customerId) {
        return new ArrayList<>();
    }

    private List<Order> getObjects(String json) throws IOException {
        return json == null ? new ArrayList<>() : objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
