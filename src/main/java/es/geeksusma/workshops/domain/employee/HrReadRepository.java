package es.geeksusma.workshops.domain.employee;

public interface HrReadRepository {

    Departments getAllDepartments();

    Employees withNoDepartments();

    Employees byDepartment(Department department);
}
