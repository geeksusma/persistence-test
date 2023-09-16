package es.geeksusma.workshops.domain.employee;

import es.geeksusma.workshops.domain.core.Id;
import es.geeksusma.workshops.domain.core.Name;

import java.util.Objects;

public class Employee {

    private final Id id;

    private final Name name;

    private final Departments departments;

    private Employee(Id id, Name name, Departments departments) {
        this.id = id;
        this.name = name;
        this.departments = departments;
    }

    public static Employee of(String id, String firstName, String lastName) {
        return new Employee(Id.of(id), Name.of(firstName, lastName), Departments.empty());
    }

    public void enroll(Department department) {
        departments.add(department);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) &&
                Objects.equals(name, employee.name) &&
                Objects.equals(departments, employee.departments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, departments);
    }
}
