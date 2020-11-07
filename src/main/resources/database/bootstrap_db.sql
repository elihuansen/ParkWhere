DELETE FROM carpark;

INSERT INTO carpark
    (id, name, address) VALUES
    (1, 'Test Carpark A', 'test_address_a'),
    (2, 'Test Carpark B', 'test_address_b'),
    (3, 'Test Carpark C', 'test_address_c');

INSERT INTO block_rate
    (id, carpark_id, start_day, start_time, end_day, end_time, amount, block_mins, is_part_thereof)
    VALUES
    (1, 1, 1, '00:00', 5, '23:59', 1.2, 30, true),
    (1, 1, 6, '00:00', 6, '23:59', 2.4, 30, true),
    (2, 2, 1, '00:00', 2, '23:59', 1.2, 30, true),
    (3, 3, 1, '00:00', 2, '23:59', 1.2, 30, true);
