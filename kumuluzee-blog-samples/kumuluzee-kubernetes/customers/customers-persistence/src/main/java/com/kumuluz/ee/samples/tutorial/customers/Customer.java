package com.kumuluz.ee.samples.tutorial.customers;

import com.kumuluz.ee.samples.tutorial.orders.Order;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "customer")
@NamedQueries(value =
        {
                @NamedQuery(name = "Customer.getAll", query = "SELECT c FROM customer c")
        })
@UuidGenerator(name = "idGenerator")
public class Customer {

    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String address;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Transient
    private List<Order> orders;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}