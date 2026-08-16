CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,

    geo_data GEOMETRY -- komplexe darstellung: punkt, line, polygon, multi-polygon
);


-- init inserts:
INSERT INTO users (name, role, password)
VALUES (
'testadmin',
'ADMIN',
'$2a$10$WYqa6.61Zs6tYy93sVFC9uhEVwZDszD6QbiV6PrFphZYBpVo1rXGm'
);

INSERT INTO locations (title, description, geo_data)
VALUES (
'testinit',
'das ist ein test',
ST_GeomFromText('POLYGON((10.667 51.760, 10.680 51.760, 10.680 51.770, 10.667 51.770, 10.667 51.760))', 4326)
);

INSERT INTO locations (title, description, geo_data)
VALUES (
'testinit der zweite',
'das ist ein zweiter test',
ST_SetSRID(ST_MakePoint(10.6745, 51.7652), 4326)
);