CREATE TABLE veterinarians (
  id INT IDENTITY PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20) NOT NULL
);

CREATE TABLE pets (
  id INT IDENTITY PRIMARY KEY,
  name VARCHAR(30) NOT NULL
);

CREATE TABLE visits (
  id INT IDENTITY PRIMARY KEY,
  pet_id INT NOT NULL,
  visit_date DATE,
  description VARCHAR(255),
  veterinarian_id INT,
  FOREIGN KEY (pet_id) REFERENCES pets(id),
  FOREIGN KEY (veterinarian_id) REFERENCES veterinarians(id)
);
