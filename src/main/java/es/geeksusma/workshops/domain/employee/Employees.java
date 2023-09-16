package es.geeksusma.workshops.domain.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Employees {

    private final List<Employee> values;

    private Employees() {
        values = new ArrayList<>();
    }

    public static Employees asEmpty() {
     return empty();
    }

    public void add(Employee employee) {
        values.add(employee);
    }

    private static Employees empty() {
        return new Employees();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employees)) return false;
        Employees employees = (Employees) o;
        return Objects.equals(values, employees.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    public void enroll(Department department) {
        values.forEach(employee -> employee.enroll(department));
    }
}
