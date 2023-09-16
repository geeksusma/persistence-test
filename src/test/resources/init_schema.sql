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
