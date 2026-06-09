# Visual Gallery 📸

**Visual Gallery** adalah sebuah aplikasi desktop pengelola galeri foto dan video berdesain modern (terinspirasi dari VSCO) yang dibangun menggunakan Java. Proyek ini dikembangkan sebagai **Tugas Besar Pemrograman Objek 2 (PO2)** dengan menerapkan prinsip *Object-Oriented Programming* (OOP) secara ketat, serta menggunakan arsitektur MVC (*Model-View-Controller*) dan *Repository Pattern*.

---

## ✨ Fitur Utama

### Untuk Pengguna (User)
*   **Autentikasi Aman:** Registrasi dan Login dengan sistem hashing password menggunakan *BCrypt*.
*   **Manajemen Profil:** Mengubah detail profil, bio, foto profil, serta password. Fitur *Remember Me* untuk login yang lebih cepat.
*   **Upload Karya:** Mendukung unggahan Media (Foto: `.jpg`, `.png`, `.webp` & Video: `.mp4`) menggunakan fitur *Drag-and-Drop*. Proses otomatisasi pembuatan *thumbnail* (Thumbnailator).
*   **Eksplorasi (Feed):** Jelajahi karya orang lain, lakukan pencarian berdasarkan judul/tag/username, urutkan berdasarkan yang terbaru/terpopuler, serta filter jenis media.
*   **Interaksi Sosial:** Berikan *Like* dan tinggalkan Komentar pada postingan pengguna lain.
*   **Notifikasi Real-time:** Dapatkan pemberitahuan ketika postingan Anda disukai atau dikomentari.
*   **Personalisasi Tampilan:** Dukungan fitur *Dark Mode* dan *Light Mode* (berkat *FlatLaf*).

### Untuk Administrator (Admin)
*   **Dashboard Statistik:** Visualisasi data sistem secara *real-time* menggunakan *JFreeChart* (Distribusi Media, Rasio User/Admin, dll).
*   **Manajemen User:** Melihat daftar pengguna, mencari akun, menonaktifkan akun, atau menghapus pengguna secara permanen. Fitur menambahkan Admin baru.
*   **Manajemen Konten:** Memantau semua postingan yang diunggah dan dapat menghapus postingan yang melanggar aturan.

---

## 🛠️ Teknologi yang Digunakan

*   **Bahasa Pemrograman:** Java 17+
*   **GUI Framework:** Java Swing
*   **Tema UI:** [FlatLaf](https://www.formdev.com/flatlaf/) (Flat Look and Feel)
*   **Arsitektur & Desain:** MVC (*Model-View-Controller*), *Repository Pattern*, *Singleton Pattern*.
*   **Database:** MySQL 8.0+
*   **Database Driver & Pooling:** MySQL Connector/J, JDBC, (disiapkan untuk HikariCP)
*   **Keamanan:** JBCrypt (Password Hashing)
*   **Pemrosesan Media:** Thumbnailator (Image Resizing & Cropping)
*   **Visualisasi Data:** JFreeChart (Admin Dashboard Charts)
*   **Build Tool:** Maven

---

## 🚀 Panduan Menjalankan Program

Ikuti langkah-langkah di bawah ini untuk mengonfigurasi dan menjalankan Visual Gallery di komputer Anda.

### 1. Prasyarat Sistem
Pastikan perangkat Anda telah terinstall perangkat lunak berikut:
1.  **Java Development Kit (JDK) 17** atau versi lebih baru.
2.  **MySQL Server** (XAMPP / MySQL Workbench / MAMP).
3.  **Maven** (Opsional, jika Anda menjalankan lewat terminal). IDE modern seperti IntelliJ IDEA, Eclipse, atau VS Code umumnya sudah memilikinya.

### 2. Konfigurasi Database (MySQL)
Proyek ini membutuhkan database relasional. Semua struktur tabel, *trigger*, dan *stored procedure* telah disiapkan.

1. Buka MySQL / phpMyAdmin Anda.
2. Buat database baru bernama `visual_gallery_db`:
   ```sql
   CREATE DATABASE visual_gallery_db;
   USE visual_gallery_db;
   ```
3. Eksekusi skema database. Anda dapat menemukan file skema di:
   `src/main/resources/database/schema.sql`
   *(Copy semua isi file tersebut dan jalankan di SQL Console / phpMyAdmin, atau import file tersebut secara langsung).*

### 3. Konfigurasi Koneksi Proyek
Hubungkan aplikasi dengan database MySQL lokal Anda.

1. Buka file `src/main/resources/config/db.properties`.
2. Sesuaikan kredensial username dan password dengan milik Anda:
   ```properties
   db.url=jdbc:mysql://localhost:3306/visual_gallery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.username=root
   db.password=password_mysql_anda # Kosongkan jika XAMPP default (db.password=)
   db.pool.minIdle=5
   db.pool.maxSize=20
   ```

### 4. Menjalankan Aplikasi
Anda dapat menjalankan aplikasi ini melalui **IDE** atau **Terminal/Command Prompt**.

#### Opsi A: Menjalankan melalui IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Buka proyek ini di IDE Anda.
2. Tunggu hingga Maven selesai mengunduh semua *dependencies* (Sync `pom.xml`).
3. Cari file `Main.java` yang berada di:
   `src/main/java/com/visualgallery/Main.java`
4. Klik kanan pada file tersebut dan pilih **"Run 'Main.main()'"**.

#### Opsi B: Menjalankan melalui Maven (Terminal/PowerShell)
1. Buka terminal di direktori *root* proyek (tempat `pom.xml` berada).
2. Bersihkan dan *compile* proyek:
   ```bash
   mvn clean install
   ```
3. Jalankan aplikasi menggunakan Maven `exec` plugin:
   ```bash
   mvn exec:java -Dexec.mainClass="com.visualgallery.Main"
   ```

---

## 🔑 Akun Uji Coba

Setelah menjalankan skema `schema.sql`, sebuah akun Administrator *default* akan otomatis dibuat. Anda dapat menggunakannya untuk mencoba panel Admin:

*   **Email:** `admin@visualgallery.com`
*   **Password:** `admin123`

Untuk mencoba fitur reguler, silakan klik tombol **"Daftar Sekarang"** di halaman awal untuk membuat akun baru.

---

## 📁 Struktur Penyimpanan File
Aplikasi ini secara otomatis akan membuat folder di sistem komputer pengguna untuk menyimpan file gambar dan video yang diunggah. Direktori utama berada di direktori `Home` pengguna:
*   **Windows:** `C:\Users\[NamaUser]\VisualGallery\uploads\`
*   **Mac/Linux:** `~/VisualGallery/uploads/`

Di dalam folder tersebut akan ada pemisahan untuk `photos/`, `videos/`, `thumbnails/`, dan `profiles/`.

---

> **Dibuat dengan ❤️ untuk Tugas Besar Pemrograman Objek 2.**
