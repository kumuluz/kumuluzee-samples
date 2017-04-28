package com.kumuluz.ee.samples.microservices.simple.models;

import javax.persistence.*;
import java.util.List;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
@Entity
@Table(name = "books")
@NamedQuery(name = "Book.findAll", query = "SELECT b FROM Book b")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    private String author;

    @OneToMany(mappedBy="book")
    private List<Order> bookOrders;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Order> getBookOrders() {
        return bookOrders;
    }

    public void setBookOrders(List<Order> bookOrders) {
        this.bookOrders = bookOrders;
    }
}
