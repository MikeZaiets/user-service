INSERT into address (country, city, street, house_number, apt_number, zipcode)
VALUES ('Country1', 'City1', 'Street1', 1, 1, 1),
       ('Country2', 'City2', 'Street2', 1, 1, 2),
       ('Country3', 'City3', 'Street3', 1, 1, 3),
       ('Country4', 'City4', 'Street4', 1, 1, 4),
       ('Country5', 'City5', 'Street5', 1, 1, 5)
ON CONFLICT DO NOTHING;

INSERT into users (email, first_name, last_name, phone, birth_date, address_id)
VALUES ('User1@gmail.com', 'User1', 'User1', '1234567890', '2000-02-02', 1),
       ('User2@gmail.com', 'User2', 'User2', '2345678901', '2002-02-02', 2),
       ('User3@gmail.com', 'User3', 'User3', '3456789012', '2002-02-02', 3),
       ('User4@gmail.com', 'User4', 'User4', '4567890123', '2002-02-02', 4),
       ('User5@gmail.com', 'User5', 'User5', '5678901234', '2002-02-02', 5)
ON CONFLICT DO NOTHING;
