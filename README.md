# persistence-test
Repo to show how to write persistence tests easily

Precondition:
Add the driver for postgresql

```
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
		</dependency>
```

1 - Add the next set of dependencies:

```
        <!-- this is the core of Testcontainers -->
        <dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>testcontainers</artifactId>
			<version>1.19.0</version>
			<scope>test</scope>
		</dependency>
		
		<!-- this is to have support for jdbc + postgresql containers -->

		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>postgresql</artifactId>
			<version>1.19.0</version>
			<scope>test</scope>
		</dependency>

        <!-- special support for junit 5 -->

		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>junit-jupiter</artifactId>
			<version>1.19.0</version>
			<scope>test</scope>
		</dependency>
```

2 - Define a container for the database:

```
    @Container
    static final GenericContainer databaseContainer = new PostgreSQLContainer(
            "postgres:9.6.12")
            .withDatabaseName("product_db")
            .withUsername("user")
            .withPassword("password");

```
 3 - Do not forget the `@Testcontainers` annotation
 
 4 - To initialise the database with some schema creation
 
 ```
-- create
CREATE TABLE department (
  id varchar NOT NULL PRIMARY KEY
);

CREATE TABLE employee (
  id varchar NOT NULL PRIMARY KEY,
  first_name varchar NOT NULL,
  last_name varchar NOT NULL
);

CREATE TABLE employee_department(
  employee_id varchar NOT NULL REFERENCES employee(id) ON DELETE CASCADE,
  department_id varchar NOT NULL REFERENCES department(id) ON DELETE CASCADE,
  CONSTRAINT employee_department_pkey PRIMARY KEY (employee_id, department_id)
);


--populate

INSERT INTO department (id) values  ('HR');
INSERT INTO department (id) values ('ENGINEERING');
INSERT INTO department (id) values ('SALES');
INSERT INTO department (id) values ('TALENT');
 ```
 
 ```
   @Container
     private static final JdbcDatabaseContainer databaseContainer = new PostgreSQLContainer(
             "postgres:9.6.12")
             .withDatabaseName("product_db")
             .withUsername("user")
             .withPassword("password")
             .withInitScript("init_schema.sql");
```
 
 5 - Retrieve the datasource and inject it into the repository
 ```
 //Add the @SpringBootTest annotation to load the context
 @SpringBootTest
 class HrReadSQLRepositoryTest
 ...
 
 //Inject the JdbcTemplate dependency
 
     @Autowired
     private JdbcTemplate jdbcTemplate;
 
 
 //Add the jdbc url user/password to the application properties dinamically
     @DynamicPropertySource
     static void configureProperties(DynamicPropertyRegistry registry) {
         registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
         registry.add("spring.datasource.username", databaseContainer::getUsername);
         registry.add("spring.datasource.password", databaseContainer::getPassword);
     }
     
 //Ensure before each test the jdbcTemplate is injected in the repository under test
 
     @BeforeEach
     void setUp() {
         hrReadRepository = new HrReadSQLRepository(jdbcTemplate);
     }
 ```
 
 6 - Let's code!
 
 These are the tests:
 
 ```java
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
```

Can you implement the repository from the scratch?

```java
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

```