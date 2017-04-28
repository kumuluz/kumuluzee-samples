package com.kumuluz.ee.samples.microservices.simple.models;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
@Entity
@Table(name = "orders")
@NamedQuery(name = "BookOrder.findAll", query = "SELECT o FROM Order o")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @ManyToOne
    @JoinColumn(name="book_id")
    private Book book;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
