package es.geeksusma.workshops.adapters.out;

import es.geeksusma.workshops.domain.employee.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class HrReadSQLRepositoryTest {


    @Container
    private static final JdbcDatabaseContainer databaseContainer = new PostgreSQLContainer(
            "postgres:9.6.12")
            .withDatabaseName("product_db")
            .withUsername("user")
            .withPassword("password")
            .withInitScript("init_schema.sql");


    @Autowired
    private JdbcTemplate jdbcTemplate;

    private HrReadRepository hrReadRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", databaseContainer::getUsername);
        registry.add("spring.datasource.password", databaseContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        hrReadRepository = new HrReadSQLRepository(jdbcTemplate);
    }

    @Test
    void should_retrieveAllDepartmentsOrderedById_when_getAll() {

        //when
        Departments departments = hrReadRepository.getAllDepartments();

        //then
        assertThat(departments).extracting("values").asList()
                .containsExactly(Department.ENGINEERING, Department.HR, Department.SALES, Department.TALENT);
    }

    @Test
    void should_returnEmptyEmployees_when_allEmployeesAreAssignedToADepartment() {
        //given
        insertEmployee("barbara-liskov", "barbara", "liskov", Department.SALES);
        insertEmployee("kent-beck", "kent", "beck", Department.ENGINEERING);
        insertEmployee("martin-fowler", "martin", "fowler", Department.SALES);
        insertEmployee("emily-bache", "emily", "bache", Department.TALENT, Department.ENGINEERING);


        //when
        Employees employees = hrReadRepository.withNoDepartments();

        //then
        assertThat(employees).isEqualTo(Employees.asEmpty());
    }

    @Test
    void should_discardEmployeesAssignedToDepartments_when_onlyUnAssignedAreRequested() {
        //given
        final Employee barbara = barbara();
        final Employee martin = martin();
        final Employee kent = kent(Department.ENGINEERING);
        final Employee emily = emily(Department.TALENT);
        insertEmployee("barbara-liskov", "barbara", "liskov");
        insertEmployee("kent-beck", "kent", "beck", Department.ENGINEERING);
        insertEmployee("martin-fowler", "martin", "fowler");
        insertEmployee("emily-bache", "emily", "bache", Department.TALENT);


        //when
        Employees employees = hrReadRepository.withNoDepartments();

        //then
        assertThat(employees).extracting("values").asList().containsExactly(barbara, martin).doesNotContain(kent, emily);
        deleteEmployees("barbara-liskov", "kent-beck", "martin-fowler", "emily-bache");
    }

    @Test
    void should_retrieveEmployeesAssignedToADepartment_when_onlyAssigneesAreRequested() {
        //given
        final Employee barbara = barbara();
        final Employee martin = martin();
        final Employee kent = kent(Department.ENGINEERING);
        final Employee emily = emily(Department.TALENT);
        insertEmployee("barbara-liskov", "barbara", "liskov");
        insertEmployee("kent-beck", "kent", "beck", Department.ENGINEERING);
        insertEmployee("martin-fowler", "martin", "fowler");
        insertEmployee("emily-bache", "emily", "bache", Department.TALENT);


        //when
        Employees employees = hrReadRepository.byDepartment(Department.TALENT);

        //then
        assertThat(employees).extracting("values").asList().containsOnly(emily).doesNotContain(barbara, martin, kent);
        deleteEmployees("barbara-liskov", "kent-beck", "martin-fowler", "emily-bache");
    }


    private void deleteEmployees(String... ids) {
        Arrays.stream(ids).forEach(id ->
                jdbcTemplate.update("DELETE FROM  employee WHERE id=?", id));
    }

    private void insertEmployee(String id, String firstName, String lastName, Department... departments) {
        jdbcTemplate.update("INSERT INTO employee (id, first_name, last_name) VALUES (?,?,?)", id, firstName, lastName);
        Arrays.stream(departments).forEach(department -> jdbcTemplate.update("INSERT INTO employee_department (employee_id, department_id) VALUES (?,?)", id, department.name()));

    }


    private Employee kent(Department... departments) {
        final Employee kent = Employee.of("kent-beck", "kent", "beck");
        Arrays.stream(departments).forEach(kent::enroll);
        return kent;
    }

    private Employee emily(Department... departments) {
        final Employee emily = Employee.of("emily-bache", "emily", "bache");
        Arrays.stream(departments).forEach(emily::enroll);
        return emily;
    }

    private Employee martin() {
        return Employee.of("martin-fowler", "martin", "fowler");
    }

    private Employee barbara() {
        return Employee.of("barbara-liskov", "barbara", "liskov");
    }
}