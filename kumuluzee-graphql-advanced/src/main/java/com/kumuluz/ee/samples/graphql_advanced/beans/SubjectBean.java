package com.kumuluz.ee.samples.graphql_advanced.beans;

import com.kumuluz.ee.samples.graphql_advanced.classes.Subject;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// This bean represents a different data source (outside of JPA).
@ApplicationScoped
public class SubjectBean {
    private Map<Integer, List<Subject>> allSubjects;
    private List<Subject> subjectList;

    @PostConstruct
    public void init() {
        allSubjects = new HashMap<>();
        subjectList = new ArrayList<>();

        subjectList.add(new Subject(subjectList.size(),"Maths", "P22"));
        subjectList.add(new Subject(subjectList.size(),"Programming in Java", "PA"));
        subjectList.add(new Subject(subjectList.size(),"Programming in Python", "P1"));

        List<Subject> subjectList1 = new ArrayList<>();
        subjectList1.add(subjectList.get(0));
        subjectList1.add(subjectList.get(1));
        allSubjects.put(63170000, subjectList1);

        List<Subject> subjectList2 = new ArrayList<>();
        subjectList2.add(subjectList.get(2));
        allSubjects.put(63170001, subjectList2);
    }

    public List<Subject> getSubjects(Integer studentNumber) {
        List<Subject> subjects = allSubjects.get(studentNumber);
        if (subjects != null) {
            return subjects;
        } else {
            return new ArrayList<>();
        }
    }

    public void addSubject(Subject s) {
        s.setId(subjectList.size());
        subjectList.add(s);
    }

    public boolean assignSubject(Integer studentNumber, Integer subjectId) {
        List<Subject> subjects = allSubjects.get(studentNumber);
        Subject subject = getSubjectById(subjectId);
        if (subjects != null) {
            if(subjects.contains(subject)) {
                return false;
            } else {
                subjects.add(subject);
            }
        } else {
            List<Subject> newSubjects = new ArrayList<>();
            newSubjects.add(subject);
            allSubjects.put(studentNumber, newSubjects);
        }
        return true;
    }

    private Subject getSubjectById(Integer id) {
        return subjectList.get(id);
    }

    public PaginationWrapper<Subject> getSubjectList(Pagination pagination, Sort sort, Filter filter) {
        return GraphQLUtils.process(subjectList, pagination, sort, filter);
    }
}
