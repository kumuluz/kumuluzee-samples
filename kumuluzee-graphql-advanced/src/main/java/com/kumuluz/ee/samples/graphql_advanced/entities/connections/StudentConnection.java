package com.kumuluz.ee.samples.graphql_advanced.entities.connections;

import com.kumuluz.ee.samples.graphql_advanced.entities.Student;

import java.util.List;

public class StudentConnection extends ConnectionBase<Student> {

    public StudentConnection(List<Student> edges, long totalCount) {
        super(edges, totalCount);
    }
}
