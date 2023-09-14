package es.geeksusma.workshops.adapters.out;

import es.geeksusma.workshops.domain.employee.Department;
import es.geeksusma.workshops.domain.employee.Departments;
import es.geeksusma.workshops.domain.employee.Employees;
import es.geeksusma.workshops.domain.employee.HrReadRepository;
import org.springframework.jdbc.core.JdbcTemplate;

class HrReadSQLRepository implements HrReadRepository {

    private final JdbcTemplate jdbcTemplate;

    HrReadSQLRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Departments getAllDepartments() {
        return null;
    }

    @Override
    public Employees withNoDepartments() {
        return null;
    }

    @Override
    public Employees byDepartment(Department department) {
        return null;
    }
}
