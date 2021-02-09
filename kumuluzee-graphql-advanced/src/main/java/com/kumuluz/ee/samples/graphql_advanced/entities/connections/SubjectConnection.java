package com.kumuluz.ee.samples.graphql_advanced.entities.connections;

import com.kumuluz.ee.samples.graphql_advanced.dtos.Subject;

import java.util.List;

public class SubjectConnection extends ConnectionBase<Subject> {

    public SubjectConnection(List<Subject> edges, long totalCount) {
        super(edges, totalCount);
    }
}
