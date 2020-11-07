INSERT INTO carpark
    (id, name, address)
VALUES
    (1, 'Test Carpark A', 'test_address_a'),
    (2, 'Test Carpark B', 'test_address_b'),
    (3, 'Test Carpark C', 'test_address_c');

INSERT INTO block_rate
    (id, carpark_id, start_day, start_time, end_day, end_time, amount, block_mins, is_part_thereof)
VALUES
    (1, 1, 1, '00:00', 5, '23:59', 1.2, 30, true),
    (2, 1, 6, '00:00', 6, '23:59', 2.4, 30, true),
    (3, 1, 7, '00:00', 7, '23:59', 4, 0, true),
    (4, 2, 1, '00:00', 2, '23:59', 1.2, 30, true),
    (5, 3, 1, '00:00', 2, '23:59', 1.2, 30, true);

INSERT INTO first_block_rate
    (id, carpark_id, amount, block_mins, is_part_thereof)
VALUES
    (1, 1, 4, 120, true);

------------- Postgres primary key annoyance -------------
SELECT setval(pg_get_serial_sequence('carpark', 'id'), coalesce(max(id) + 1, 1), false) FROM carpark;
SELECT setval(pg_get_serial_sequence('block_rate', 'id'), coalesce(max(id) + 1, 1), false) FROM block_rate;
SELECT setval(pg_get_serial_sequence('first_block_rate', 'id'), coalesce(max(id) + 1, 1), false) FROM first_block_rate;
