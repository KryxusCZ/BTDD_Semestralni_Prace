INSERT INTO users (name, email, role)
SELECT 'Admin', 'admin@example.com', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

INSERT INTO users (name, email, role)
SELECT 'Jan Novak', 'jan@example.com', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'jan@example.com');

INSERT INTO rooms (name, location, capacity, hourly_rate, opening_time, closing_time, active)
SELECT 'Mistnost A', 'Budova 1', 10, 100.00, '08:00:00', '18:00:00', true
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE name = 'Mistnost A');
