-- src/main/resources/db/h2/V1_0_2__add_visit_weight.sql
ALTER TABLE visits ADD COLUMN weight DECIMAL(5,2);
