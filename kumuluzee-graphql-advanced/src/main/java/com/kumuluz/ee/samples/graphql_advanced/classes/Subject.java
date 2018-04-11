package com.kumuluz.ee.samples.graphql_advanced.classes;

import io.leangen.graphql.annotations.GraphQLIgnore;

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

    @GraphQLIgnore
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
