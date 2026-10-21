DROP DATABASE IF EXISTS energy_audit_db;
CREATE DATABASE energy_audit_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE energy_audit_db;

CREATE TABLE object_item (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             code VARCHAR(40) NOT NULL,
                             name VARCHAR(160) NOT NULL,
                             address VARCHAR(200) NOT NULL
);

CREATE TABLE energy_audit (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              object_id BIGINT NOT NULL,
                              audit_date VARCHAR(20) NOT NULL,
                              auditor VARCHAR(120) NOT NULL,
                              notes VARCHAR(500),
                              FOREIGN KEY (object_id) REFERENCES object_item(id) ON DELETE CASCADE
);

CREATE TABLE measure (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         audit_id BIGINT NOT NULL,
                         title VARCHAR(200) NOT NULL,
                         state VARCHAR(30) NOT NULL,
                         f1 DOUBLE NOT NULL,
                         f2 DOUBLE NOT NULL,
                         f3 DOUBLE NOT NULL,
                         integral_index DOUBLE NOT NULL,
                         FOREIGN KEY (audit_id) REFERENCES energy_audit(id) ON DELETE CASCADE
);

CREATE TABLE consumption_record (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    object_id BIGINT NOT NULL,
                                    resource_type VARCHAR(100) NOT NULL,
                                    consumption_value DOUBLE NOT NULL,
                                    period VARCHAR(20) NOT NULL,
                                    FOREIGN KEY (object_id) REFERENCES object_item(id) ON DELETE CASCADE
);

INSERT INTO object_item (code, name, address) VALUES
                                                  ('OBJ-01', 'Головний корпус НУПП', 'м. Полтава, Першотравневий проспект, 24'),
                                                  ('OBJ-02', 'Гуртожиток №2', 'м. Полтава, вул. Нагірна, 5');

INSERT INTO energy_audit (object_id, audit_date, auditor, notes) VALUES
                                                                     (1, '2026-02-15', 'ТОВ ЕнергоКонсалт', 'Первинний комплексний енергоаудит'),
                                                                     (2, '2026-03-10', 'КП Енергосервіс', 'Обстеження системи опалення');

INSERT INTO measure (audit_id, title, state, f1, f2, f3, integral_index) VALUES
    (1, 'Утеплення фасаду та заміна вікон', 'PROPOSED', 75.0, 60.0, 80.0, 71.0);

INSERT INTO consumption_record (object_id, resource_type, consumption_value, period) VALUES
                                                                                         (1, 'Електроенергія', 12500.5, '2026-01'),
                                                                                         (2, 'Природний газ', 3400.0, '2026-01');