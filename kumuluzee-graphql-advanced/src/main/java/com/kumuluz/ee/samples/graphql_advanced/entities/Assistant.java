package com.kumuluz.ee.samples.graphql_advanced.entities;

import javax.persistence.*;

@Entity
@NamedQueries(
        @NamedQuery(name = "getAllAssistans", query = "SELECT a FROM Assistant a")
)
public class Assistant extends Person {
    @Column
    private Double popularity;

    public Assistant() {
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }
}
