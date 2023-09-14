package es.geeksusma.workshops.domain.employee;

import java.util.ArrayList;
import java.util.List;

public class Employees {

    private final List<Employee> values;

    private Employees() {
        values = new ArrayList<>();
    }

    static Employees asEmpty() {
     return empty();
    }

    void add(Employee employee) {
        values.add(employee);
    }

    private static Employees empty() {
        return new Employees();
    }
}
