CREATE TABLE IF NOT EXISTS employees (
                                         id BIGSERIAL PRIMARY KEY,
                                         first_name VARCHAR(50),
                                         last_name VARCHAR(50),
                                         phone VARCHAR(20),
                                         dni VARCHAR(8),
                                         address VARCHAR(150),
                                         birth_date DATE
);

CREATE TABLE IF NOT EXISTS offices (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(100),
                                       location VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS employee_office (
                                               employee_id BIGINT REFERENCES employees(id) ON DELETE CASCADE,
                                               office_id BIGINT REFERENCES offices(id) ON DELETE CASCADE,
                                               PRIMARY KEY (employee_id, office_id)
);