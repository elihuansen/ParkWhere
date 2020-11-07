-------------- Entities --------------

DROP TABLE IF EXISTS carpark;
CREATE TABLE carpark(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(100),
    address VARCHAR(150),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT (NOW() AT TIME ZONE 'utc'),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT (NOW() AT TIME ZONE 'utc')
);

DROP TABLE IF EXISTS block_rate;
CREATE TABLE block_rate (
    id SERIAL NOT NULL PRIMARY KEY,
    carpark_id INTEGER REFERENCES carpark(id),

    start_day SMALLINT, -- 1 = Monday, 7 = Sunday
    start_time TIME NOT NULL,
    end_day SMALLINT, -- 1 = Monday, 7 = Sunday
    end_time TIME NOT NULL,

    amount NUMERIC(2) NOT NULL,
    block_mins INTEGER NOT NULL,
    is_part_thereof BOOLEAN DEFAULT TRUE
);

DROP TABLE IF EXISTS first_block_rate;
CREATE TABLE first_block_rate(
    id SERIAL NOT NULL PRIMARY KEY,
    carpark_id INTEGER REFERENCES carpark(id),

    amount NUMERIC(2) NOT NULL,
    block_mins INTEGER NOT NULL,
    is_part_thereof BOOLEAN DEFAULT TRUE
);


-------------- Functions --------------

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = (NOW() AT TIME ZONE 'utc');
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-------------- Triggers --------------

CREATE TRIGGER set_timestamp
BEFORE UPDATE ON carpark
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

