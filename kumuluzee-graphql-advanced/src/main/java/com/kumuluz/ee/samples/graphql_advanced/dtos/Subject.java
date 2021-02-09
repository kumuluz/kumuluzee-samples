package com.kumuluz.ee.samples.graphql_advanced.dtos;

import org.eclipse.microprofile.graphql.Ignore;

public class Subject {

    private Integer id;
    private String name;
    private String classroom;

    public Subject(Integer id, String name, String classroom) {
        this.id = id;
        this.name = name;
        this.classroom = classroom;
    }

    public Subject() {

    }

    public Integer getId() {
        return id;
    }

    @Ignore
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
}
