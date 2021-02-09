package com.kumuluz.ee.samples.graphql_advanced.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.kumuluz.ee.samples.graphql_advanced.entities.Lecturer;
import com.kumuluz.ee.samples.graphql_advanced.entities.connections.LecturerConnection;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequestScoped
public class LecturerBean {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Lecturer> getLecturerList(QueryParameters queryParameters) {
        //use pagination, but do not return any metadata
        return JPAUtils.queryEntities(entityManager, Lecturer.class, queryParameters);
    }

    public LecturerConnection getLecturerConnection(QueryParameters queryParameters) {

        List<Lecturer> result = JPAUtils.queryEntities(entityManager, Lecturer.class, queryParameters);
        long resultCount = JPAUtils.queryEntitiesCount(entityManager, Lecturer.class, queryParameters);

        return new LecturerConnection(result, resultCount);
    }
}
