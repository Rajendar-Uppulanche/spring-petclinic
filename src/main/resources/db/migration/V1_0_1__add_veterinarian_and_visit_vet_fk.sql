-- V1_0_1__add_veterinarian_and_visit_vet_fk.sql

-- Create veterinarians table
CREATE TABLE veterinarians (
  id INT IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  contact_information VARCHAR(255) NOT NULL
);

-- Add veterinarian_id to visits table
ALTER TABLE visits ADD COLUMN veterinarian_id INT;

-- Add foreign key constraint to visits table
ALTER TABLE visits ADD CONSTRAINT fk_visits_veterinarians FOREIGN KEY (veterinarian_id) REFERENCES veterinarians(id);

-- Optional: Add an index for the foreign key
CREATE INDEX idx_visits_veterinarian_id ON visits (veterinarian_id);