-- Insert routes
INSERT INTO routes (route_name) VALUES
('Route 1'),
('Route 2'),
('Route 3'),
('Route 4');

-- Insert stops
INSERT INTO stops (route_id, stop_order, stop_name) VALUES
(1, 1, 'Main Gate'),
(1, 2, 'Social Science Building'),
(1, 3, 'New Arts Building'),
(1, 4, 'Bot-tola'),
(1, 5, 'CS Building'),

(2, 1, 'Chourongi'),
(2, 2, 'Medical'),
(2, 3, 'SRJ Hall'),
(2, 4, 'CS Building'),
(2, 5, 'Library'),

(3, 1, 'Prantik'),
(3, 2, 'CS Building'),
(3, 3, 'MH Hall'),
(3, 4, 'Cafeteria'),
(3, 5, 'Tarzan'),

(4, 1, 'Prantik'),
(4, 2, 'Uttorpara'),
(4, 3, 'Tarzan'),
(4, 4, 'Cafeteria'),
(4, 5, 'MH Hall');

-- Insert buses
INSERT INTO buses (bus_name, route_id, current_stop_order) VALUES
('Bus 1', 1, 2),
('Bus 2', 2, 3),
('Bus 3', 3, 3),
('Bus 4', 4, 3);

-- Insert users
INSERT INTO users (name, email, password, role) VALUES
('Admin', 'admin@juniv.edu', '12345', 'admin'),
('Afia Jahin Rupali', 'rupali@juniv.edu', '12345', 'student');
