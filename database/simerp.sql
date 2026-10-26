-- =========================================================
-- SIMERP - Sistem Informasi Manajemen ERP (PT Aljera)
-- Versi ringkas: 11 tabel
-- Modul: Umum, Master Data, Penjualan, Pembelian, Stok, Keuangan
-- =========================================================

DROP DATABASE IF EXISTS simerp;
CREATE DATABASE simerp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE simerp;

-- ---------------------------------------------------------
-- MODUL UMUM
-- ---------------------------------------------------------
CREATE TABLE roles (
  role_id   INT AUTO_INCREMENT PRIMARY KEY,
  role_name VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE users (
  user_id    INT AUTO_INCREMENT PRIMARY KEY,
  role_id    INT NOT NULL,
  username   VARCHAR(50)  NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,          -- SHA-256 (PasswordUtil.hash)
  full_name  VARCHAR(100) NOT NULL,
  is_active  TINYINT(1)   NOT NULL DEFAULT 1,
  created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (role_id) REFERENCES roles(role_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- MASTER DATA
-- ---------------------------------------------------------
CREATE TABLE products (
  product_id     INT AUTO_INCREMENT PRIMARY KEY,
  product_code   VARCHAR(30)  NOT NULL UNIQUE,
  product_name   VARCHAR(100) NOT NULL,
  unit           VARCHAR(20)  NOT NULL,
  purchase_price DECIMAL(15,2) NOT NULL DEFAULT 0,
  selling_price  DECIMAL(15,2) NOT NULL DEFAULT 0,
  stock          INT NOT NULL DEFAULT 0,         -- stok saat ini (satu gudang)
  minimum_stock  INT NOT NULL DEFAULT 0,         -- batas peringatan stok menipis
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE customers (
  customer_id   INT AUTO_INCREMENT PRIMARY KEY,
  customer_name VARCHAR(100) NOT NULL,
  phone         VARCHAR(20),
  email         VARCHAR(100),
  address       TEXT,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE suppliers (
  supplier_id   INT AUTO_INCREMENT PRIMARY KEY,
  supplier_name VARCHAR(100) NOT NULL,
  phone         VARCHAR(20),
  email         VARCHAR(100),
  address       TEXT,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- MODUL PENJUALAN
-- Saat status jadi 'Dikonfirmasi': stok berkurang + pemasukan tercatat
-- ---------------------------------------------------------
CREATE TABLE sales_orders (
  so_id        INT AUTO_INCREMENT PRIMARY KEY,
  so_number    VARCHAR(30) NOT NULL UNIQUE,
  customer_id  INT NOT NULL,
  user_id      INT NOT NULL,
  order_date   DATE NOT NULL,
  status       ENUM('Draft','Dikonfirmasi','Dibatalkan') NOT NULL DEFAULT 'Draft',
  total_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
  FOREIGN KEY (user_id)     REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE sales_order_items (
  so_item_id INT AUTO_INCREMENT PRIMARY KEY,
  so_id      INT NOT NULL,
  product_id INT NOT NULL,
  quantity   INT NOT NULL,
  price      DECIMAL(15,2) NOT NULL,
  subtotal   DECIMAL(15,2) NOT NULL,
  FOREIGN KEY (so_id)      REFERENCES sales_orders(so_id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- MODUL PEMBELIAN
-- Saat status jadi 'Diterima': stok bertambah + pengeluaran tercatat
-- ---------------------------------------------------------
CREATE TABLE purchase_orders (
  po_id        INT AUTO_INCREMENT PRIMARY KEY,
  po_number    VARCHAR(30) NOT NULL UNIQUE,
  supplier_id  INT NOT NULL,
  user_id      INT NOT NULL,
  order_date   DATE NOT NULL,
  status       ENUM('Draft','Diterima','Dibatalkan') NOT NULL DEFAULT 'Draft',
  total_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
  FOREIGN KEY (user_id)     REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE purchase_order_items (
  po_item_id INT AUTO_INCREMENT PRIMARY KEY,
  po_id      INT NOT NULL,
  product_id INT NOT NULL,
  quantity   INT NOT NULL,
  price      DECIMAL(15,2) NOT NULL,
  subtotal   DECIMAL(15,2) NOT NULL,
  FOREIGN KEY (po_id)      REFERENCES purchase_orders(po_id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- MODUL STOK
-- Riwayat keluar-masuk barang (dicatat otomatis dari SO / PO)
-- ---------------------------------------------------------
CREATE TABLE stock_movements (
  movement_id    INT AUTO_INCREMENT PRIMARY KEY,
  product_id     INT NOT NULL,
  movement_type  ENUM('Masuk','Keluar') NOT NULL,
  quantity       INT NOT NULL,
  reference_type ENUM('SO','PO','Penyesuaian') NOT NULL,
  reference_id   INT,
  movement_date  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- MODUL KEUANGAN (sederhana)
-- Pemasukan dari SO, pengeluaran dari PO, atau input manual
-- ---------------------------------------------------------
CREATE TABLE transactions (
  transaction_id   INT AUTO_INCREMENT PRIMARY KEY,
  transaction_date DATE NOT NULL,
  type             ENUM('Pemasukan','Pengeluaran') NOT NULL,
  category         VARCHAR(50) NOT NULL,       -- Penjualan, Pembelian, Operasional, dll
  amount           DECIMAL(15,2) NOT NULL,
  description      VARCHAR(255),
  reference_type   ENUM('SO','PO','Manual') NOT NULL DEFAULT 'Manual',
  reference_id     INT,
  user_id          INT NOT NULL,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- =========================================================
-- DATA AWAL
-- =========================================================
INSERT INTO roles (role_id, role_name) VALUES
  (1,'Admin'), (2,'Sales'), (3,'Purchasing'), (4,'Keuangan'), (5,'Manajer');

-- password semua akun: admin123
INSERT INTO users (role_id, username, password, full_name) VALUES
  (1,'admin',     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','Administrator'),
  (2,'sales',     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','Staf Sales'),
  (3,'purchasing','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','Staf Purchasing'),
  (4,'keuangan',  '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','Staf Keuangan'),
  (5,'manajer',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','Manajer');

INSERT INTO products (product_code, product_name, unit, purchase_price, selling_price, stock, minimum_stock) VALUES
  ('PRD-001','Kemeja Katun Pria',   'pcs', 85000, 135000, 50, 10),
  ('PRD-002','Celana Chino',        'pcs', 95000, 150000, 30, 10),
  ('PRD-003','Kaos Polos',          'pcs', 35000,  65000,  8, 15),
  ('PRD-004','Jaket Denim',         'pcs',160000, 250000, 12,  5);

INSERT INTO customers (customer_name, phone, email, address) VALUES
  ('Toko Sinar Jaya',  '081234567801','sinarjaya@mail.com','Depok'),
  ('CV Maju Bersama',  '081234567802','majubersama@mail.com','Bekasi');

INSERT INTO suppliers (supplier_name, phone, email, address) VALUES
  ('PT Tekstil Nusantara','021-7778801','tekstil@mail.com','Bandung'),
  ('CV Garmen Sentosa',  '021-7778802','garmen@mail.com','Jakarta');
