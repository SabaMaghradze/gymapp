CREATE USER costigan WITH PASSWORD 'BillyCostigan';
CREATE DATABASE gymapp_local_dev OWNER costigan;

-- Staging
CREATE USER sullivan WITH PASSWORD 'ColinSullivan';
CREATE DATABASE gymapp_stg OWNER sullivan;

-- Production
CREATE USER dignam WITH PASSWORD 'SeanDignam';
CREATE DATABASE gymapp_prod OWNER dignam;