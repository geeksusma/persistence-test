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