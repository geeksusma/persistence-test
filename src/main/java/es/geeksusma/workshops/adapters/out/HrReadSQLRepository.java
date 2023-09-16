package es.geeksusma.workshops.adapters.out;

import es.geeksusma.workshops.domain.employee.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

class HrReadSQLRepository implements HrReadRepository {

    private final JdbcTemplate jdbcTemplate;

    HrReadSQLRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Departments getAllDepartments() {
        final List<String> resultSet = jdbcTemplate.queryForList("SELECT id FROM department ORDER BY id", String.class);
        return toDepartment(resultSet);
    }


    @Override
    public Employees withNoDepartments() {
        final List<Map<String, Object>> resultSet = jdbcTemplate.queryForList("SELECT id, first_name, last_name FROM employee WHERE id not in (SELECT employee_id FROM employee_department)");
        return toEmployeesWithNoDepartments(resultSet);
    }

    private Employees toEmployeesWithNoDepartments(List<Map<String, Object>> resultSet) {
        final Employees employees = Employees.asEmpty();

        resultSet.forEach(row -> {
            final Employee employee = Employee.of((String) row.get("id"), (String) row.get("first_name"), (String) row.get("last_name"));
            employees.add(employee);
        });

        return employees;
    }

    @Override
    public Employees byDepartment(Department department) {
        final List<Map<String, Object>> resultSet = jdbcTemplate.queryForList("SELECT id, first_name, last_name FROM employee WHERE id in (SELECT employee_id FROM employee_department WHERE department_id=?)", department.name());
        final Employees employeesForDepartment = toEmployeesWithNoDepartments(resultSet);
        employeesForDepartment.enroll(department);
        return employeesForDepartment;
    }

    private Departments toDepartment(List<String> resultSet) {
        final Departments departments = Departments.empty();
        resultSet.forEach(department -> departments.add(Department.valueOf(department)));
        return departments;
    }
}
