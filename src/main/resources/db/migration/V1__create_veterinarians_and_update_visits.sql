-- Create veterinarians table
CREATE TABLE veterinarians (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  contact_information VARCHAR(255)
) engine=InnoDB;

-- Add veterinarian_id to visits table
ALTER TABLE visits
ADD COLUMN veterinarian_id INT(4) UNSIGNED;

-- Add foreign key constraint
ALTER TABLE visits
ADD CONSTRAINT fk_visits_veterinarians FOREIGN KEY (veterinarian_id) REFERENCES veterinarians(id);

-- Optional: Add an index for performance
CREATE INDEX idx_visits_veterinarian_id ON visits (veterinarian_id);