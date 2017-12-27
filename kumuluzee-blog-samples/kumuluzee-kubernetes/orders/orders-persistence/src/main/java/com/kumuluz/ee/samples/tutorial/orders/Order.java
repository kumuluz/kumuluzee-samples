package com.kumuluz.ee.samples.tutorial.orders;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "orders")
@NamedQueries(value =
        {
                @NamedQuery(name = "Order.getAll", query = "SELECT o FROM orders o"),
                @NamedQuery(name = "Order.findByCustomer", query = "SELECT o FROM orders o WHERE o.customerId = " +
                        ":customerId")
        })
@UuidGenerator(name = "idGenerator")
public class Order {

    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private String title;

    private String description;

    private Date submitted;

    @Column(name = "customer_id")
    private String customerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }
}
