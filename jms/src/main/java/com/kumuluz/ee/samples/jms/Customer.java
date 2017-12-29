package com.kumuluz.ee.samples.jms;

import java.io.Serializable;

/**
 * @author Dejan Ognjenović
 * @since 2.4.0
 */
public class Customer implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
