package es.geeksusma.workshops.domain.employee;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
}