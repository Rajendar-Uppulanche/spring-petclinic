CREATE TABLE specialties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE vet_specialties (
    vet_id BIGINT NOT NULL,
    specialty_id BIGINT NOT NULL,
    PRIMARY KEY (vet_id, specialty_id),
    FOREIGN KEY (vet_id) REFERENCES vets(id),
    FOREIGN KEY (specialty_id) REFERENCES specialties(id)
);

-- Optional: Insert some initial data for specialties
INSERT INTO specialties (name) VALUES ('Radiology');
INSERT INTO specialties (name) VALUES ('Surgery');
INSERT INTO specialties (name) VALUES ('Dentistry');
INSERT INTO specialties (name) VALUES ('Internal Medicine');
INSERT INTO specialties (name) VALUES ('Cardiology');

-- Optional: Link some existing vets to specialties (assuming 'vets' table already exists)
-- This part would depend on existing data and vet IDs. For a fresh start, it might be empty.
-- For demonstration, let's assume vet with ID 1 has Radiology and Surgery, vet with ID 2 has Dentistry.
-- INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (1, (SELECT id FROM specialties WHERE name = 'Radiology'));
-- INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (1, (SELECT id FROM specialties WHERE name = 'Surgery'));
-- INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (2, (SELECT id FROM specialties WHERE name = 'Dentistry'));