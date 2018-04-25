package com.kumuluz.ee.samples.graphql_advanced.graphql;

import com.kumuluz.ee.samples.graphql_advanced.beans.FacultyBean;
import com.kumuluz.ee.samples.graphql_advanced.beans.SubjectBean;
import com.kumuluz.ee.samples.graphql_advanced.classes.Subject;
import com.kumuluz.ee.samples.graphql_advanced.entities.Student;
import com.kumuluz.ee.samples.graphql_advanced.providers.DefaultIndexProvider;
import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@GraphQLClass
public class StudentResolvers {
    @Inject
    private FacultyBean facultyBean;

    @Inject
    private SubjectBean subjectBean;

    @PersistenceContext
    private EntityManager entityManager;

    // additional data
    @GraphQLQuery
    public List<Subject> subjects(@GraphQLContext Student student, @GraphQLArgument(name="sort") Sort sort) {
        List<Subject> subjectList = subjectBean.getSubjects(student.getStudentNumber());
        return GraphQLUtils.processWithoutPagination(subjectList, sort);
    }

    // queries
    @GraphQLQuery
    public PaginationWrapper<Student> allStudentsJPA(@GraphQLArgument(name="pagination") Pagination pagination, @GraphQLArgument(name="sort") Sort sort, @GraphQLArgument(name="filter") Filter filter, @GraphQLEnvironment ResolutionEnvironment resolutionEnvironment) {
        return GraphQLUtils.process(entityManager, Student.class, resolutionEnvironment, pagination, sort, filter);
    }

    @GraphQLQuery
    public PaginationWrapper<Student> allStudents(@GraphQLArgument(name="pagination") Pagination pagination, @GraphQLArgument(name="sort") Sort sort, @GraphQLArgument(name="filter") Filter filter) {
        return GraphQLUtils.process(facultyBean.getStudentList(), pagination, sort, filter);
    }

    @GraphQLQuery
    public Student studentByIndex(@GraphQLArgument(name="index", defaultValueProvider = DefaultIndexProvider.class) Integer index) {
        return GraphQLUtils.processWithoutPagination(entityManager, Student.class).get(index);
    }

    // mutations
    @GraphQLMutation
    public Student addStudent(@GraphQLArgument(name="student") Student student) {
        facultyBean.addStudent(student);
        return student;
    }
}
