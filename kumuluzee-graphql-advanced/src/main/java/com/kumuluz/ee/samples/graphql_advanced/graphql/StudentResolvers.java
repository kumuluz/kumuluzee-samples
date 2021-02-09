package com.kumuluz.ee.samples.graphql_advanced.graphql;

import com.kumuluz.ee.graphql.mp.utils.GraphQLUtils;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.QueryStringDefaults;
import com.kumuluz.ee.rest.utils.StreamUtils;
import com.kumuluz.ee.samples.graphql_advanced.beans.StudentBean;
import com.kumuluz.ee.samples.graphql_advanced.beans.SubjectBean;
import com.kumuluz.ee.samples.graphql_advanced.dtos.Subject;
import com.kumuluz.ee.samples.graphql_advanced.entities.Student;
import com.kumuluz.ee.samples.graphql_advanced.entities.connections.StudentConnection;
import org.eclipse.microprofile.graphql.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@GraphQLApi
@ApplicationScoped
public class StudentResolvers {

    @Inject
    private StudentBean studentBean;

    @Inject
    private SubjectBean subjectBean;

    @Inject
    private QueryStringDefaults qsd;

    // additional data
    @Name("subjects")
    public List<Subject> getStudentSubjects(@Source Student student, String sort) {

        QueryParameters qp = GraphQLUtils.queryParametersBuilder()
                .withQueryStringDefaults(qsd)
                .withOrder(sort)
                .build();

        List<Subject> subjectList = subjectBean.getSubjects(student.getStudentNumber());
        return StreamUtils.queryEntities(subjectList, qp);
    }

    // queries
    @Query
    public StudentConnection getStudentsConnection(Long limit, Long offset, String sort, String filter) {

        QueryParameters qp = GraphQLUtils.queryParametersBuilder()
                .withQueryStringDefaults(qsd)
                .withLimit(limit)
                .withOffset(offset)
                .withOrder(sort)
                .withFilter(filter)
                .build();

        return studentBean.getStudentConnection(qp);
    }

    @Query
    public Student studentById(@NonNull Integer id) {

        return studentBean.getStudentById(id);
    }

    // mutations
    @Mutation
    public Student createStudent(@NonNull Student student) {

        return studentBean.createStudent(student);
    }
}
