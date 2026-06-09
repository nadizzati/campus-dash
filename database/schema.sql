CREATE DATABASE campus_dash;
\c campus_dash;

CREATE TABLE student_accounts (
    id_student           BIGSERIAL PRIMARY KEY,
    username             VARCHAR(50) NOT NULL UNIQUE,
    password_hash        VARCHAR(255) NOT NULL,
    total_koin_terkumpul INTEGER DEFAULT 0,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE task_submissions (
    id_sesi          BIGSERIAL PRIMARY KEY,
    id_student       BIGINT NOT NULL REFERENCES student_accounts(id_student) ON DELETE CASCADE,
    status_tugas     VARCHAR(20) NOT NULL CHECK (status_tugas IN ('In_Progress','Completed','Failed','Timeout')),
    waktu_tersisa    INTEGER NOT NULL DEFAULT 120,
    koin_dikumpulkan INTEGER DEFAULT 0,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at      TIMESTAMP
);

CREATE INDEX idx_task_submissions_student ON task_submissions(id_student);
CREATE INDEX idx_task_submissions_status  ON task_submissions(status_tugas);

INSERT INTO student_accounts (username, password_hash, total_koin_terkumpul) VALUES
    ('mahasiswa01', 'hashed_password_1', 0),
    ('mahasiswa02', 'hashed_password_2', 50),
    ('mahasiswa03', 'hashed_password_3', 120);