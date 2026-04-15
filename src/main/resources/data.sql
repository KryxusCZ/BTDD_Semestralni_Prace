INSERT INTO users (name, email, role) VALUES ('Admin', 'admin@example.com', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, role) VALUES ('Jan Novak', 'jan@example.com', 'USER')
ON CONFLICT (email) DO NOTHING;

INSERT INTO rooms (name, location, capacity, hourly_rate, opening_time, closing_time, active)
VALUES ('Mistnost A', 'Budova 1', 10, 100.00, '08:00:00', '18:00:00', true)
ON CONFLICT DO NOTHING;
