package com.kumuluz.ee.samples.graphql_advanced.entities.connections;

import com.kumuluz.ee.samples.graphql_advanced.entities.Lecturer;

import java.util.List;

public class LecturerConnection extends ConnectionBase<Lecturer> {

    public LecturerConnection(List<Lecturer> edges, long totalCount) {
        super(edges, totalCount);
    }
}
