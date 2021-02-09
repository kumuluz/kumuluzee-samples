package com.kumuluz.ee.samples.graphql_advanced.entities;

import com.kumuluz.ee.samples.graphql_advanced.enums.LecturerStatus;

import javax.persistence.*;

@Entity
@NamedQueries(
        @NamedQuery(name="getAllLecturers", query = "SELECT l FROM Lecturer l")
)
public class Lecturer extends Person {

    @Column
    private String location;
    @Column
    private LecturerStatus status;
    @ManyToOne
    private Assistant assistant;

    public Lecturer() {
    }

    public String getLocation() {
        return location;
    }

    public LecturerStatus getStatus() {
        return status;
    }

    public Assistant getAssistant() {
        return assistant;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(LecturerStatus status) {
        this.status = status;
    }

    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;
    }
}
