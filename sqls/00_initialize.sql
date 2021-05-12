USE sample_db;

DROP TABLE IF EXISTS t_department;
CREATE TABLE t_department(
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  location VARCHAR(128) NOT NULL
);

DROP TABLE IF EXISTS t_employee;
create table t_employee(
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  job VARCHAR(128) NOT NULL,
  hire_date DATE NOT NULL,
  salary BIGINT NOT NULL,
  department_id INT NOT NULL
);

INSERT INTO t_department (name, location) VALUES ("Server-side Team", "Tokyo");
INSERT INTO t_department (name, location) VALUES ("Mobile Team", "Kyoto");

INSERT INTO t_employee (name, job, hire_date, salary, department_id) VALUES ("Kotlin 太郎", "Fullstack Engineer", "2020-04-01", 10000, 1);
INSERT INTO t_employee (name, job, hire_date, salary, department_id) VALUES ("Android 次郎", "Android Engineer", "2020-05-01", 10000, 2);
