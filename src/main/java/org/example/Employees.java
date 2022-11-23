package org.example;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "employees")             //the local name for the XML element from the employees.xml file
@XmlAccessorType(XmlAccessType.FIELD)           //not to trigger the getter/setter methods during the unmarshalling
public class Employees {
    @XmlElement(name = "employee")              //to specify that the xml element <employee> is the employee object
    private List<Employee> employees = null;

    public Employees() {
    }

    public List<Employee> getEmployees() {
        return this.employees;
    }
}
