package es.geeksusma.workshops.domain.employee;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeTest {


    @Test
    void should_createWithEmptyDepartment_when_createEmployee() {
        assertThat(Employee.of("abc", "barbara", "liskov"))
                .satisfies(employee -> {
                    assertThat(employee).extracting("id.value").isEqualTo("abc");
                    assertThat(employee).extracting("name.fullName").isEqualTo("barbara liskov");
                    assertThat(employee).extracting("departments").isEqualTo(Departments.empty());
                });
    }

    @Test
    void should_appendDepartment_when_departmentGiven() {
        //given
        final Employee barbara = Employee.of("abc", "barbara", "liskov");

        //when
        barbara.enroll(Department.ENGINEERING);
        barbara.enroll(Department.TALENT);

        //then
        assertThat(barbara).extracting("departments.values").asList().containsExactly(Department.ENGINEERING, Department.TALENT);
    }
}