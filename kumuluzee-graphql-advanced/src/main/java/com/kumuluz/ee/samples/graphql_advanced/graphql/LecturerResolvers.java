package com.kumuluz.ee.samples.graphql_advanced.graphql;

import com.kumuluz.ee.samples.graphql_advanced.beans.FacultyBean;
import com.kumuluz.ee.samples.graphql_advanced.entities.Lecturer;
import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;

import javax.inject.Inject;
import java.util.List;

@GraphQLClass
public class LecturerResolvers {
    @Inject
    private FacultyBean facultyBean;

    @GraphQLQuery
    public List<Lecturer> allLecturers(@GraphQLArgument(name="pagination") Pagination pagination, @GraphQLArgument(name="sort") Sort sort) {
        return facultyBean.getLecturerList(pagination, sort);
    }

    @GraphQLQuery
    public PaginationWrapper<Lecturer> allLecturersManullyPagination(@GraphQLArgument(name="pagination") Pagination pagination, @GraphQLArgument(name="sort") Sort sort, @GraphQLArgument(name="filter") Filter filter) {
        return facultyBean.getLecturerListManually(pagination, sort, filter);
    }

}
