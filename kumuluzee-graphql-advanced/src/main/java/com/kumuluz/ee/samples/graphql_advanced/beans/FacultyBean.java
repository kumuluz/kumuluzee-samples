package com.kumuluz.ee.samples.graphql_advanced.beans;

import com.kumuluz.ee.samples.graphql_advanced.entities.Lecturer;
import com.kumuluz.ee.samples.graphql_advanced.entities.Student;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class FacultyBean {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Student> getStudentList() {
        return entityManager.createNamedQuery("getAllStudents", Student.class).getResultList();
    }

    public List<Lecturer> getLecturerList(Pagination pagination, Sort sort) {
        //use pagination, but do not return any metadata
        //let graphql perform JPA callss
        return GraphQLUtils.process(entityManager, Lecturer.class, pagination, sort).getResult();
    }

    public PaginationWrapper<Lecturer> getLecturerListManually(Pagination pagination, Sort sort, Filter filter) {
        //use JPAUtils manually
        QueryParameters queryParameters = GraphQLUtils.queryParameters(pagination, sort, filter);
        List<Lecturer> subjectList = JPAUtils.queryEntities(entityManager, Lecturer.class, queryParameters);
        Long count = JPAUtils.queryEntitiesCount(entityManager, Lecturer.class, queryParameters);
        return GraphQLUtils.wrapList(subjectList, pagination, count.intValue());
    }

    @Transactional
    public Student addStudent(Student s) {
        s.setEnrolled(new Date());
        if(s != null) {
            entityManager.persist(s);
        }
        return s;
    }
}