CREATE TABLE vets (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30)
);
CREATE INDEX vets_last_name ON vets (last_name);

CREATE TABLE specialties (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX specialties_name ON specialties (name);

CREATE TABLE vet_specialties (
  vet_id       INTEGER NOT NULL,
  specialty_id INTEGER NOT NULL,
  FOREIGN KEY (vet_id) REFERENCES vets (id),
  FOREIGN KEY (specialty_id) REFERENCES specialties (id),
  UNIQUE (vet_id, specialty_id)
);

CREATE TABLE types (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX types_name ON types (name);

CREATE TABLE owners (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  address    VARCHAR(255),
  city       VARCHAR(80),
  telephone  VARCHAR(20)
);
CREATE INDEX owners_last_name ON owners (last_name);

CREATE TABLE pets (
  id         INTEGER IDENTITY PRIMARY KEY,
  name       VARCHAR(30),
  birth_date DATE,
  type_id    INTEGER NOT NULL,
  owner_id   INTEGER NOT NULL,
  FOREIGN KEY (type_id) REFERENCES types (id),
  FOREIGN KEY (owner_id) REFERENCES owners (id)
);
CREATE INDEX pets_name ON pets (name);

CREATE TABLE visits (
  id          INTEGER IDENTITY PRIMARY KEY,
  pet_id      INTEGER NOT NULL,
  visit_date  DATE,
  description VARCHAR(255),
  FOREIGN KEY (pet_id) REFERENCES pets (id)
);
CREATE INDEX visits_pet_id ON visits (pet_id);

CREATE TABLE preventive_care (
  id         INTEGER IDENTITY PRIMARY KEY,
  pet_id     INTEGER NOT NULL,
  care_date  DATE NOT NULL,
  type       VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  notes      VARCHAR(255),
  FOREIGN KEY (pet_id) REFERENCES pets (id)
);
CREATE INDEX preventive_care_pet_id ON preventive_care (pet_id);