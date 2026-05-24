-- Database Schema Campus Dash

CREATE DATABASE campus_dash;
\c campus_dash;

CREATE TABLE student_accounts (
    id_student      BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    total_koin_terkumpul INTEGER DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE task_submissions (
    id_sesi         BIGSERIAL PRIMARY KEY,
    id_student      BIGINT NOT NULL REFERENCES student_accounts(id_student) ON DELETE CASCADE,
    status_tugas    VARCHAR(20) NOT NULL CHECK (status_tugas IN ('IN_PROGRESS','COMPLETED','FAILED','TIMEOUT')),
    waktu_tersisa   INTEGER NOT NULL DEFAULT 300,
    koin_dikumpulkan INTEGER DEFAULT 0,
    level           INTEGER DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at     TIMESTAMP
);

CREATE TABLE item_shop (
    id_item         BIGSERIAL PRIMARY KEY,
    nama_item       VARCHAR(100) NOT NULL,
    deskripsi       TEXT,
    harga_koin      INTEGER NOT NULL,
    efek_item       VARCHAR(50),
    is_available    BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student_items (
    id              BIGSERIAL PRIMARY KEY,
    id_student      BIGINT NOT NULL REFERENCES student_accounts(id_student) ON DELETE CASCADE,
    id_item         BIGINT NOT NULL REFERENCES item_shop(id_item) ON DELETE CASCADE,
    purchased_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_student, id_item)
);

CREATE INDEX idx_task_submissions_student ON task_submissions(id_student);
CREATE INDEX idx_task_submissions_status  ON task_submissions(status_tugas);
CREATE INDEX idx_student_items_student    ON student_items(id_student);

INSERT INTO student_accounts (username, password_hash, total_koin_terkumpul) VALUES
    ('mahasiswa01', 'hashed_password_1', 0),
    ('mahasiswa02', 'hashed_password_2', 50),
    ('mahasiswa03', 'hashed_password_3', 120);

INSERT INTO item_shop (nama_item, deskripsi, harga_koin, efek_item) VALUES
    ('Speed Shoes',    'Meningkatkan kecepatan gerak 2x selama 10 detik', 15, 'SPEED_BOOST'),
    ('Time Extender',  'Menambah 30 detik ke timer',                      20, 'TIME_BONUS'),
    ('Ghost Shield',   'Imun dari enemy selama 5 detik',                  25, 'SHIELD'),
    ('Coin Magnet',    'Menarik koin dalam radius 3 tile',                 30, 'COIN_MAGNET');
