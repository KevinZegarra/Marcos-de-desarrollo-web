-- ═══════════════════════════════════════════════════════════
--  AY FITNESS GYM — Script SQL inicial
--  Ejecutar SOLO si usas ddl-auto=create (no update)
--  Con ddl-auto=update, Hibernate crea las tablas solo.
--  Este script sirve como referencia y para datos de prueba.
-- ═══════════════════════════════════════════════════════════

-- 1. Crear base de datos
CREATE DATABASE IF NOT EXISTS ayfitnessgym
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE ayfitnessgym;

-- ─────────────────────────────────────────────
-- 2. Tablas (referencia — Hibernate las crea)
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS planes (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(50)    NOT NULL UNIQUE,
  precio        DECIMAL(10,2)  NOT NULL,
  duracion_dias INT            NOT NULL DEFAULT 30,
  descripcion   VARCHAR(500),
  beneficios    TEXT,
  activo        TINYINT(1)     NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS usuarios (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  rol        VARCHAR(20)  NOT NULL DEFAULT 'ADMIN',
  activo     TINYINT(1)   NOT NULL DEFAULT 1,
  cliente_id BIGINT       NULL,
  INDEX idx_username (username)
);

CREATE TABLE IF NOT EXISTS clientes (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre             VARCHAR(100) NOT NULL,
  apellido           VARCHAR(100) NOT NULL,
  dni                VARCHAR(20)  UNIQUE,
  telefono           VARCHAR(20),
  correo             VARCHAR(150) UNIQUE,
  fecha_inscripcion  DATE         NOT NULL,
  fecha_vencimiento  DATE,
  estado             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
  plan_id            BIGINT       NULL,
  INDEX idx_estado (estado),
  INDEX idx_vencimiento (fecha_vencimiento),
  FOREIGN KEY (plan_id) REFERENCES planes(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS pagos (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  cliente_id         BIGINT         NOT NULL,
  plan_id            BIGINT         NOT NULL,
  monto              DECIMAL(10,2)  NOT NULL,
  fecha_pago         DATE           NOT NULL,
  fecha_inicio       DATE           NOT NULL,
  fecha_vencimiento  DATE           NOT NULL,
  estado             VARCHAR(20)    NOT NULL DEFAULT 'PAGADO',
  metodo_pago        VARCHAR(50)    DEFAULT 'Efectivo',
  observaciones      VARCHAR(255),
  INDEX idx_fecha_pago (fecha_pago),
  INDEX idx_cliente (cliente_id),
  FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
  FOREIGN KEY (plan_id)    REFERENCES planes(id)   ON DELETE RESTRICT
);

-- ─────────────────────────────────────────────
-- 3. Datos de prueba (opcionales)
--    El DataInitializer ya crea los planes y el admin.
--    Estos son clientes de ejemplo para testing.
-- ─────────────────────────────────────────────

-- Insertar clientes de prueba DESPUÉS de que la app corra al menos una vez
-- (así existen los planes con sus IDs)

/*
INSERT INTO clientes (nombre, apellido, dni, telefono, correo, fecha_inscripcion, fecha_vencimiento, estado, plan_id)
VALUES
  ('Carlos',  'Ramírez',  '45678901', '+51987654321', 'carlos@email.com',  CURDATE() - INTERVAL 15 DAY, CURDATE() + INTERVAL 15 DAY, 'ACTIVO',    (SELECT id FROM planes WHERE nombre='Pro')),
  ('María',   'Torres',   '32145678', '+51976543210', 'maria@email.com',   CURDATE() - INTERVAL 25 DAY, CURDATE() + INTERVAL 5  DAY, 'POR_VENCER',(SELECT id FROM planes WHERE nombre='Básico')),
  ('Diego',   'Paredes',  '12398745', '+51965432109', 'diego@email.com',   CURDATE() - INTERVAL 40 DAY, CURDATE() - INTERVAL 10 DAY, 'VENCIDO',   (SELECT id FROM planes WHERE nombre='Elite')),
  ('Ana',     'Gutiérrez','78945612', '+51954321098', 'ana@email.com',     CURDATE() - INTERVAL 20 DAY, CURDATE() + INTERVAL 10 DAY, 'ACTIVO',    (SELECT id FROM planes WHERE nombre='Pro')),
  ('Luis',    'Mendoza',  '65412398', '+51943210987', 'luis@email.com',    CURDATE() - INTERVAL 8  DAY, CURDATE() + INTERVAL 22 DAY, 'ACTIVO',    (SELECT id FROM planes WHERE nombre='Básico')),
  ('Sofía',   'Castro',   '98712345', '+51932109876', 'sofia@email.com',   CURDATE() - INTERVAL 25 DAY, CURDATE() + INTERVAL 5  DAY, 'POR_VENCER',(SELECT id FROM planes WHERE nombre='Elite'));

INSERT INTO pagos (cliente_id, plan_id, monto, fecha_pago, fecha_inicio, fecha_vencimiento, estado, metodo_pago)
SELECT c.id, c.plan_id, p.precio,
       c.fecha_inscripcion,
       c.fecha_inscripcion,
       c.fecha_vencimiento,
       CASE WHEN c.fecha_vencimiento < CURDATE() THEN 'VENCIDO' ELSE 'PAGADO' END,
       'Efectivo'
FROM clientes c
JOIN planes p ON p.id = c.plan_id;
*/

-- ─────────────────────────────────────────────
-- 4. Verificar datos
-- ─────────────────────────────────────────────
-- SELECT * FROM planes;
-- SELECT * FROM usuarios;
-- SELECT c.nombre, c.apellido, p.nombre as plan, c.fecha_vencimiento, c.estado FROM clientes c JOIN planes p ON p.id=c.plan_id;
