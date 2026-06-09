# Campus Dash: Deadline Pursuit

Game berbasis grid 2D yang dibuat dengan **LibGDX** (frontend) dan **Spring Boot** (backend). Player berperan sebagai mahasiswa yang harus mengumpulkan koin nilai sebelum deadline habis, sambil menghindari aslab dan dosen yang berpatroli.

---

## Play Online

Tersedia di itch.io, tidak perlu install apapun:

**[https://nadizzati.itch.io/campusdash](https://nadizzati.itch.io/campusdash)**

---

## Struktur Project

```
FE-OOP/
├── assets/
├── core/src/main/java/com/nadia/caslab/
│   ├── entity/
│   │   ├── Player.java
│   │   ├── Enemy.java
│   │   ├── Coin.java
│   │   └── TileMap.java
│   ├── factory/
│   │   └── CoinFactory.java
│   ├── observer/
│   │   ├── GameEventManager.java
│   │   └── GameObserver.java
│   ├── strategy/
│   │   ├── MovementStrategy.java
│   │   ├── RandomMovementStrategy.java
│   │   ├── PatrolMovementStrategy.java
│   │   └── ChaseMovementStrategy.java
│   ├── state/
│   │   ├── DoorContext.java
│   │   └── DoorState.java
│   ├── command/
│   │   ├── Command.java
│   │   ├── MoveCommands.java
│   │   └── InputHandler.java
│   ├── screen/
│   │   ├── SplashScreen.java
│   │   ├── LoginScreen.java
│   │   ├── MainMenuScreen.java
│   │   ├── TutorialScreen.java
│   │   ├── GameScreen.java
│   │   ├── GameOverScreen.java
│   │   └── LeaderboardScreen.java
│   ├── ui/
│   │   └── HUD.java
│   ├── manager/
│   │   └── SoundManager.java
│   ├── network/
│   │   └── ApiClient.java
│   └── game/
│       ├── CampusDashGame.java
│       └── GameConstants.java
├── lwjgl3/src/main/java/com/nadia/caslab/lwjgl3/
│   ├── Lwjgl3Launcher.java
│   └── StartupHelper.java
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md

BE-OOP/
├── src/main/java/com/caslab/
│   ├── config/
│   │   └── GlobalExceptionHandler.java
│   ├── controller/
│   │   ├── StudentAccountController.java
│   │   └── TaskSubmissionController.java
│   ├── dto/
│   │   └── Dto.java
│   ├── entity/
│   │   ├── StudentAccount.java
│   │   └── TaskSubmission.java
│   ├── repository/
│   │   ├── StudentAccountRepository.java
│   │   └── TaskSubmissionRepository.java
│   ├── service/
│   │   ├── StudentAccountService.java
│   │   └── TaskSubmissionService.java
│   └── CampusDashApplication.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md

database/
└── schema.sql
```

---

## Design Pattern yang Digunakan

| Pattern | Kelas | Keterangan |
|---------|-------|------------|
| Strategy | `MovementStrategy`, `RandomMovementStrategy`, `PatrolMovementStrategy`, `ChaseMovementStrategy`, `GuardMovementStrategy` | Perilaku gerak enemy yang bisa diganti runtime |
| Observer | `GameEventManager`, `GameObserver`, `HUD` | HUD otomatis update saat event game terjadi |
| Factory Method | `CoinFactory` | Spawn koin dengan posisi valid dan distribusi merata |
| State | `DoorContext`, `LockedState`, `OpenState` | Pintu lab berubah state saat koin cukup |
| Command | `InputHandler`, `Command` | Input player dikemas sebagai command object |
| Template Method | `GameScreen`, `MainMenuScreen`, `LeaderboardScreen`, `TutorialScreen`, `SplashScreen` | Lifecycle screen ditentukan framework, implementasi disesuaikan tiap screen |

---

## Frontend (LibGDX)

### Prasyarat
- Java 17+
- IntelliJ IDEA
- Gradle 7+

### Cara Menjalankan
```bash
# Di root folder project
.\gradlew.bat lwjgl3:run
```

Atau lewat IntelliJ: Run konfigurasi `Lwjgl3Launcher`.

### Gameplay
- **Tujuan**: Kumpulkan koin sejumlah `COINS_TO_WIN` lalu masuk ke pintu lab
- **Kontrol**: `W` atas, `S` bawah, `A` kiri, `D` kanan
- **Enemy**:
  - Aslab (merah) — bergerak random
  - Dosen (ungu) — patroli vertikal, lebih cepat
- **Kena enemy** → waktu berkurang 10 detik + invincible 2 detik
- **Waktu habis** → GAGAL

### Konfigurasi Game (`GameConstants.java`)

| Konstanta | Nilai | Keterangan |
|-----------|-------|------------|
| `TILE_SIZE` | 48 | Ukuran tile dalam pixel |
| `GRID_COLS` | 24 | Jumlah kolom map |
| `GRID_ROWS` | 16 | Jumlah baris map |
| `COINS_TO_WIN` | 20 | Koin yang dibutuhkan untuk menang |
| `GAME_TIME_SEC` | 90 | Waktu permainan (detik) |
| `TIME_PENALTY` | 10 | Penalti waktu saat kena enemy |
| `PLAYER_SPEED` | 5.0 | Kecepatan player (tile/detik) |
| `ENEMY_SPEED` | 2.5 | Kecepatan enemy (tile/detik) |

### Sistem Koin (Object Pooling)
- Hanya 10 koin aktif di map dalam satu waktu
- Saat koin diambil, objek yang sama di-reuse dan dipindahkan ke posisi baru
- Tidak ada `new Coin()` saat respawn, lebih hemat memori

### Tile Types

| Konstanta | Nilai | Keterangan |
|-----------|-------|------------|
| `TILE_FLOOR` | 0 | Lantai, bisa dilewati |
| `TILE_WALL` | 1 | Dinding, tidak bisa dilewati |
| `TILE_DOOR` | 2 | Pintu lab, hanya bisa dilewati saat terbuka |
| `TILE_DESK` | 3 | Meja, tidak bisa dilewati |
| `TILE_COMPUTER` | 4 | Komputer, tidak bisa dilewati |

---

## Backend (Spring Boot)

### Prasyarat
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Langkah Setup
```bash
# 1. Buat database
psql -U postgres -f ../database/schema.sql

# 2. Edit konfigurasi database
# File: src/main/resources/application.properties
# Sesuaikan: username, password, nama database

# 3. Jalankan backend
mvn spring-boot:run
# Berjalan di http://localhost:8080
```

### API Endpoints

#### Student Accounts
| Method | URL | Keterangan |
|--------|-----|------------|
| POST | /api/students/register | Registrasi akun |
| GET | /api/students | Semua akun |
| GET | /api/students/{id} | Detail akun |
| DELETE | /api/students/{id} | Hapus akun |
| GET | /api/students/leaderboard | Top skor |

#### Game Sessions
| Method | URL | Keterangan |
|--------|-----|------------|
| POST | /api/sessions/start/{id} | Mulai sesi game |
| POST | /api/sessions/submit | Submit hasil game |
| GET | /api/sessions/history/{id} | Riwayat sesi |

#### Item Shop
| Method | URL | Keterangan |
|--------|-----|------------|
| GET | /api/items | Daftar item |
| POST | /api/items/buy | Beli item |
| GET | /api/items/student/{id} | Item milik mahasiswa |

### Test Cepat
```bash
# Registrasi
curl -X POST http://localhost:8080/api/students/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'

# Leaderboard
curl http://localhost:8080/api/students/leaderboard
```

---

## Koneksi Frontend <-> Backend

Frontend terhubung ke backend via `ApiClient.java`. Pastikan backend berjalan di `localhost:8080` sebelum menjalankan game.

Jika backend tidak tersedia, game tetap bisa dimainkan dalam **mode offline**, sesi tidak akan tersimpan ke database.
