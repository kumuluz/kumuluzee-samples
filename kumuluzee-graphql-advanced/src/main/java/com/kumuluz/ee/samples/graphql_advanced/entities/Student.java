package com.kumuluz.ee.samples.graphql_advanced.entities;

import io.leangen.graphql.annotations.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQueries(
        @NamedQuery(name = "getAllStudents", query="SELECT s FROM Student s")
)
public class Student extends Person {
    @Column(unique=true)
    private Integer studentNumber;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date enrolled;

    public Student() {
        //needs empty constructor to deserialize object when performing mutations
    }

    public Integer getStudentNumber() {
        //getters are resolvers for fields
        return studentNumber;
    }

    public Date getEnrolled() {
        return enrolled;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    @GraphQLIgnore
    public void setEnrolled(Date enrolled) {
        this.enrolled = enrolled;
    }
}
