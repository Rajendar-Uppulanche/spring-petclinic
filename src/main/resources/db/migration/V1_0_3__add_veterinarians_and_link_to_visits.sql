CREATE TABLE veterinarians (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  contact_information VARCHAR(255),
  INDEX(name)
) engine=InnoDB;

ALTER TABLE visits ADD COLUMN veterinarian_id INT(4) UNSIGNED;
ALTER TABLE visits ADD CONSTRAINT fk_visits_veterinarians FOREIGN KEY (veterinarian_id) REFERENCES veterinarians(id);
