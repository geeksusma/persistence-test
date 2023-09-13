package es.geeksusma.workshops.domain.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Departments {
    private List<Department> values;

    private Departments() {
        this.values = new ArrayList<>();
    }

    static Departments empty() {
        return new Departments();
    }

    void add(Department department) {
        this.values.add(department);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Departments)) return false;
        Departments that = (Departments) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
