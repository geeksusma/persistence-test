package es.geeksusma.workshops.domain.employee;

import es.geeksusma.workshops.domain.core.Id;
import es.geeksusma.workshops.domain.core.Name;

class Employee {

    private final Id id;

    private final Name name;

    private final Departments departments;

    private Employee(Id id, Name name, Departments departments) {
        this.id = id;
        this.name = name;
        this.departments = departments;
    }

    static Employee of(String id, String firstName, String lastName) {
        return new Employee(Id.of(id), Name.of(firstName, lastName), Departments.empty());
    }

    void enroll(Department department) {
        departments.add(department);
    }
}
