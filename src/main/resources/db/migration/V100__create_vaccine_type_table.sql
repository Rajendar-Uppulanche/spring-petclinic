-- V100__create_vaccine_type_table.sql
CREATE TABLE vaccine_types (
  id INT IDENTITY PRIMARY KEY,
  name VARCHAR(80) UNIQUE NOT NULL,
  default_next_due_interval_days INT
);
