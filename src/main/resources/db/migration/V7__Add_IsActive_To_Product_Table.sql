-- Add is_active to Product table
ALTER TABLE product 
    ADD is_active bit NOT NULL DEFAULT 1
