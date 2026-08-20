-- V101__create_vaccination_table.sql
CREATE TABLE vaccinations (
  id INT IDENTITY PRIMARY KEY,
  pet_id INT NOT NULL,
  vaccine_type_id INT NOT NULL,
  date_administered DATE NOT NULL,
  administering_vet VARCHAR(255),
  batch_number VARCHAR(255),
  next_due_date DATE,
  FOREIGN KEY (pet_id) REFERENCES pets(id),
  FOREIGN KEY (vaccine_type_id) REFERENCES vaccine_types(id)
);
