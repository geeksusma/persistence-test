package es.geeksusma.workshops.domain.employee;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeesTest {


    @Test
    void should_createWithNoValues_asEmpty() {

        assertThat(Employees.asEmpty()).extracting("values").asList().isEmpty();
    }

    @Test
    void should_appendEmployee_when_add() {
        //given
        final Employee martin = Employee.of("bce", "Martin", "Fowler");
        final Employees employees = Employees.asEmpty();

        //when
        employees.add(martin);

        //then
        assertThat(employees).extracting("values").asList().containsOnly(martin);
    }

    @Test
    void should_appendAllEmployeesToDepartment_when_enroll() {
        //given
        final Employee mary = Employee.of("fgh", "mary", "poppendieck");
        final Employee mash = Employee.of("ijk", "mashooq", "baddar");
        final Employees employees = Employees.asEmpty();
        employees.add(mary);
        employees.add(mash);

        //when
        employees.enroll(Department.ENGINEERING);
        employees.enroll(Department.TALENT);

        //then
        assertThat(employees).extracting("values").asList().extracting("departments.values").containsOnly(List.of(Department.ENGINEERING, Department.TALENT), List.of(Department.ENGINEERING, Department.TALENT));
    }
}