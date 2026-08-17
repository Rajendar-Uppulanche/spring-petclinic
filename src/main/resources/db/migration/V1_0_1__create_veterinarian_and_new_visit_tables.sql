CREATE TABLE veterinarians (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  contact_information VARCHAR(255),
  INDEX(name)
) engine=InnoDB;

CREATE TABLE visits_new (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  pet_id INT(4) UNSIGNED NOT NULL,
  visit_date DATE NOT NULL,
  description VARCHAR(255),
  veterinarian_id INT(4) UNSIGNED,
  FOREIGN KEY (veterinarian_id) REFERENCES veterinarians(id),
  INDEX(pet_id)
) engine=InnoDB;
