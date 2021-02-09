package com.kumuluz.ee.samples.graphql_advanced.graphql;

import com.kumuluz.ee.graphql.mp.utils.GraphQLUtils;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.QueryStringDefaults;
import com.kumuluz.ee.samples.graphql_advanced.beans.LecturerBean;
import com.kumuluz.ee.samples.graphql_advanced.entities.Lecturer;
import com.kumuluz.ee.samples.graphql_advanced.entities.connections.LecturerConnection;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@GraphQLApi
@ApplicationScoped
public class LecturerResolvers {

    @Inject
    private LecturerBean lecturerBean;

    @Inject
    private QueryStringDefaults qsd;

    @Query
    public List<Lecturer> getLecturers(String sort, String filter) {

        QueryParameters qp = GraphQLUtils.queryParametersBuilder()
                .withQueryStringDefaults(qsd)
                .withOrder(sort)
                .withFilter(filter)
                .build();

        return lecturerBean.getLecturerList(qp);
    }

    @Query
    public LecturerConnection getLecturersConnection(Long limit, Long offset, String sort, String filter) {

        QueryParameters qp = GraphQLUtils.queryParametersBuilder()
                .withQueryStringDefaults(qsd)
                .withLimit(limit)
                .withOffset(offset)
                .withOrder(sort)
                .withFilter(filter)
                .build();

        return lecturerBean.getLecturerConnection(qp);
    }
}
