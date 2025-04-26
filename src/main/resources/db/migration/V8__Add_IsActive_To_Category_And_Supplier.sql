-- Add is_active to Category table
ALTER TABLE category 
    ADD is_active bit NOT NULL DEFAULT 1

-- Add is_active to Supplier table
ALTER TABLE supplier 
    ADD is_active bit NOT NULL DEFAULT 1
