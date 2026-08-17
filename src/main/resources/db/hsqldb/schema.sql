-- Add indexes for visit filtering
-- For visits table
CREATE INDEX visits_date ON visits (visit_date);
CREATE INDEX visits_pet_id ON visits (pet_id);

-- For pets table (for pet name and owner lookup)
CREATE INDEX pets_name ON pets (name);
CREATE INDEX pets_owner_id ON pets (owner_id);

-- For owners table (for owner last name lookup)
CREATE INDEX owners_last_name ON owners (last_name);