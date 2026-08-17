ALTER TABLE visits ADD COLUMN vet_id INT;
ALTER TABLE visits ADD CONSTRAINT fk_visits_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
