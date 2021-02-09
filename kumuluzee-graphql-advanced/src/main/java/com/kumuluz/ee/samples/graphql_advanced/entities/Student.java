package com.kumuluz.ee.samples.graphql_advanced.entities;

import org.eclipse.microprofile.graphql.Ignore;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NamedQueries(
        @NamedQuery(name = "getAllStudents", query="SELECT s FROM Student s")
)
public class Student extends Person {

    @Column(unique=true)
    private Integer studentNumber;

    @Column
    private LocalDate enrolled;

    public Student() {
        //needs empty constructor to deserialize object when performing mutations
    }

    public Integer getStudentNumber() {
        //getters are resolvers for fields
        return studentNumber;
    }

    public LocalDate getEnrolled() {
        return enrolled;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Ignore
    public void setEnrolled(LocalDate enrolled) {
        this.enrolled = enrolled;
    }
}
