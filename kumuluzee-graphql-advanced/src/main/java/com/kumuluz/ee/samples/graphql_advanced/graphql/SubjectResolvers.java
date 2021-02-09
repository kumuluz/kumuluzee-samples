package com.kumuluz.ee.samples.graphql_advanced.graphql;

import com.kumuluz.ee.graphql.mp.utils.GraphQLUtils;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.QueryStringDefaults;
import com.kumuluz.ee.samples.graphql_advanced.beans.SubjectBean;
import com.kumuluz.ee.samples.graphql_advanced.dtos.AssignmentResult;
import com.kumuluz.ee.samples.graphql_advanced.dtos.Subject;
import com.kumuluz.ee.samples.graphql_advanced.entities.connections.SubjectConnection;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@GraphQLApi
@ApplicationScoped
public class SubjectResolvers {

    @Inject
    private SubjectBean subjectBean;

    @Inject
    private QueryStringDefaults qsd;

    // queries
    @Query
    public SubjectConnection getSubjectsConnection(Long offset, Long limit, String sort, String filter) {

        QueryParameters qp = GraphQLUtils.queryParametersBuilder()
                .withQueryStringDefaults(qsd)
                .withOffset(offset)
                .withLimit(limit)
                .withOrder(sort)
                .withFilter(filter)
                .build();

        return subjectBean.getSubjectConnection(qp);
    }

    // mutations
    @Mutation
    public Subject createSubject(@NonNull Subject subject) {
        return subjectBean.createSubject(subject);
    }

    @Mutation
    public AssignmentResult assignSubject(@NonNull Integer studentNumber, @NonNull Integer subjectId) {

        boolean result = subjectBean.assignSubject(studentNumber, subjectId);

        if (result) {
            return new AssignmentResult("Subject assigned successfully.", 200);
        } else {
            return new AssignmentResult("Could not assign subject.", 400);
        }
    }
}
