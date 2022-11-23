package org.example;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "employee")          //the local name for the XML element from the employees.xml file
@XmlAccessorType(XmlAccessType.FIELD)       //not to trigger the getter/setter methods during the unmarshalling
public class Employee {
    private Integer id;
    private String firstName;
    private String lastName;

    public Employee() {
    }

    public Integer getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
}
