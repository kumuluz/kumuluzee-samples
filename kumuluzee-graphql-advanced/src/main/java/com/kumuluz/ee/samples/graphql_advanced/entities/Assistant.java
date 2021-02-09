package com.kumuluz.ee.samples.graphql_advanced.entities;

import javax.persistence.*;

@Entity
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
