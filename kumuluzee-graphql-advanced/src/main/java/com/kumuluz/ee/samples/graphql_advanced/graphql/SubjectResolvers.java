package com.kumuluz.ee.samples.graphql_advanced.graphql;

import com.kumuluz.ee.samples.graphql_advanced.beans.SubjectBean;
import com.kumuluz.ee.samples.graphql_advanced.classes.MutationResult;
import com.kumuluz.ee.samples.graphql_advanced.classes.Subject;
import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@GraphQLClass
@ApplicationScoped
public class SubjectResolvers {
    @Inject
    private SubjectBean subjectBean;

    // queries
    @GraphQLQuery
    public PaginationWrapper<Subject> allSubjects(@GraphQLArgument(name="pagination") Pagination pagination, @GraphQLArgument(name="sort") Sort sort, @GraphQLArgument(name="filter") Filter filter) {
        return subjectBean.getSubjectList(pagination, sort, filter);
    }

    // mutations
    @GraphQLMutation
    public Subject addSubject(@GraphQLArgument(name="subject") Subject subject) {
        subjectBean.addSubject(subject);
        return subject;
    }

    @GraphQLMutation
    public MutationResult assignSubject(@GraphQLNonNull @GraphQLArgument(name="studentNumber") Integer studentNumber, @GraphQLNonNull @GraphQLArgument(name="subjectId") Integer subjectId) {
        Boolean result = subjectBean.assignSubject(studentNumber, subjectId);
        if(result) {
            return new MutationResult("Mutation completed successfully.", 200);
        } else {
            return new MutationResult("Something went wrong.", 400);
        }
    }
}
