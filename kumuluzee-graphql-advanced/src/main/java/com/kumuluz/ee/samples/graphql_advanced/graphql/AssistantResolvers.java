package com.kumuluz.ee.samples.graphql_advanced.graphql;

import com.kumuluz.ee.samples.graphql_advanced.entities.Assistant;
import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Sort;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@GraphQLClass
public class AssistantResolvers {
    @PersistenceContext
    private EntityManager em;

    @GraphQLQuery
    public List<Assistant> allAssistants(@GraphQLArgument(name="sort") Sort sort, @GraphQLEnvironment ResolutionEnvironment resolutionEnvironment) {
        return GraphQLUtils.processWithoutPagination(em, Assistant.class, resolutionEnvironment, sort);
    }
}