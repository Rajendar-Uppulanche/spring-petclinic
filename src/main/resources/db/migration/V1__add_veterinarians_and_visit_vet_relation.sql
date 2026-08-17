-- src/main/resources/db/migration/V1__add_veterinarians_and_visit_vet_relation.sql
-- This migration script adds the 'veterinarians' table and a foreign key to 'visits'.

-- Create the veterinarians table
CREATE TABLE veterinarians (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL,
  last_name  VARCHAR(30) NOT NULL,
  telephone  VARCHAR(20) NOT NULL
);
CREATE INDEX veterinarians_last_name ON veterinarians (last_name);

-- Add veterinarian_id column to visits table
ALTER TABLE visits ADD COLUMN veterinarian_id INTEGER;

-- Add foreign key constraint to visits table
ALTER TABLE visits ADD CONSTRAINT fk_visits_veterinarians FOREIGN KEY (veterinarian_id) REFERENCES veterinarians (id);

-- Optional: Populate some initial data for veterinarians
INSERT INTO veterinarians (id, first_name, last_name, telephone) VALUES (1, 'James', 'Carter', '555-1234');
INSERT INTO veterinarians (id, first_name, last_name, telephone) VALUES (2, 'Helen', 'Leary', '555-5678');
INSERT INTO veterinarians (id, first_name, last_name, telephone) VALUES (3, 'Linda', 'Douglas', '555-9012');