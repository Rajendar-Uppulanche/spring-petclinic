ALTER TABLE vets ADD COLUMN contact_information VARCHAR(255);

ALTER TABLE visits ADD COLUMN vet_id INT;
ALTER TABLE visits ADD CONSTRAINT fk_visits_vets FOREIGN KEY (vet_id) REFERENCES vets(id);
