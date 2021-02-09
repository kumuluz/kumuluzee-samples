package com.kumuluz.ee.samples.graphql_advanced.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.kumuluz.ee.samples.graphql_advanced.entities.Assistant;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequestScoped
public class AssistantBean {

    @PersistenceContext
    private EntityManager em;

    public List<Assistant> getAssistants(QueryParameters qp) {

        return JPAUtils.queryEntities(em, Assistant.class, qp);
    }
}
