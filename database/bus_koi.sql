CREATE TABLE routes(
    route_id INT AUTO_INCREMENT PRIMARY KEY,
    route_name VARCHAR(100)
);

CREATE TABLE stops(
    stop_id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT,
    stop_order INT,
    stop_name VARCHAR(100),
    FOREIGN KEY(route_id) REFERENCES routes(route_id)
);

CREATE TABLE buses(
    bus_id INT AUTO_INCREMENT PRIMARY KEY,
    bus_name VARCHAR(100),
    route_id INT,
    current_stop_order INT,
    FOREIGN KEY(route_id) REFERENCES routes(route_id)
);

CREATE TABLE users(
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100),
    role VARCHAR(20)
);
