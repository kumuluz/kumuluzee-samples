package com.kumuluz.ee.samples.graphql_advanced.graphql;

import com.kumuluz.ee.graphql.mp.utils.GraphQLUtils;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.QueryStringDefaults;
import com.kumuluz.ee.samples.graphql_advanced.beans.AssistantBean;
import com.kumuluz.ee.samples.graphql_advanced.entities.Assistant;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@GraphQLApi
@ApplicationScoped
public class AssistantResolvers {

    @Inject
    private QueryStringDefaults qsd;

    @Inject
    private AssistantBean assistantBean;

    @Query
    public List<Assistant> getAssistants(String sort) {

        QueryParameters qp = GraphQLUtils.queryParametersBuilder()
                .withQueryStringDefaults(qsd)
                .withOrder(sort)
                .build();

        return assistantBean.getAssistants(qp);
    }
}