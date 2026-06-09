# Panduan Setup Database (Visual Gallery) 🗄️

Dokumen ini berisi panduan langkah demi langkah khusus untuk mengonfigurasi dan menjalankan database MySQL untuk aplikasi **Visual Gallery** menggunakan **XAMPP**.

---

## Tahap 1: Menjalankan MySQL via XAMPP

1. Buka aplikasi **XAMPP Control Panel**.
2. Pada baris **Apache**, klik tombol **Start**. (Pastikan indikator menjadi warna hijau).
3. Pada baris **MySQL**, klik tombol **Start**. (Pastikan indikator menjadi warna hijau dan port tertulis `3306`).
4. Jika keduanya sudah hijau, server database lokal Anda sudah berjalan.

---

## Tahap 2: Membuka phpMyAdmin

1. Buka browser web Anda (Chrome, Firefox, Edge, dll).
2. Ketikkan URL berikut di *address bar*:
   👉 `http://localhost/phpmyadmin/`
3. Anda akan masuk ke halaman *dashboard* utama phpMyAdmin.

---

## Tahap 3: Membuat Database Baru

1. Di halaman phpMyAdmin, klik tab **Databases** (atau **Basis Data**) di menu navigasi atas.
2. Di bawah kolom *Create database*, ketikkan nama database berikut persis tanpa tanda kutip:
   👉 `visual_gallery_db`
3. Klik tombol **Create** (Buat). Database Anda akan muncul di daftar sebelah kiri.

---

## Tahap 4: Meng-import Skema Database (`schema.sql`)

Anda memiliki dua cara untuk memasukkan struktur tabel dan data bawaan:

### Cara A: Melalui Tab SQL (Paling Mudah)
1. Klik nama database `visual_gallery_db` di daftar sebelah kiri.
2. Buka proyek Anda di IDE (VS Code / IntelliJ) atau Notepad, lalu buka file ini:
   `d:\tugas kuliah\code s4\TubesPo2\src\main\resources\database\schema.sql`
3. **Copy (Salin)** seluruh isi teks yang ada di dalam file tersebut.
4. Kembali ke phpMyAdmin, klik tab **SQL** di menu atas.
5. **Paste (Tempel)** teks tersebut ke dalam kotak teks yang tersedia.
6. Scroll ke bawah, lalu klik tombol **Go** (Kirim) di pojok kanan bawah.

### Cara B: Melalui Fitur Import
1. Klik nama database `visual_gallery_db` di daftar sebelah kiri.
2. Klik tab **Import** di menu atas.
3. Pada bagian *File to import*, klik tombol **Choose File** (Pilih File).
4. Arahkan ke file `schema.sql` di folder proyek Anda:
   `TubesPo2 > src > main > resources > database > schema.sql`
5. Scroll ke paling bawah, biarkan pengaturan lainnya *default*, lalu klik **Import** (Kirim).

---

## Tahap 5: Pengecekan Hasil

Jika proses import berhasil, Anda akan melihat pesan sukses berwarna hijau. Di sebelah kiri, Anda akan melihat beberapa tabel telah dibuat, di antaranya:
*   `activity_logs`
*   `categories`
*   `comments`
*   `likes`
*   `notifications`
*   `post_categories`
*   `posts`
*   `saved_posts`
*   `users`

Klik pada tabel `users`. Anda akan melihat beberapa akun yang sudah otomatis terbuat:
*   **Admin:** `admin@visualgallery.com` (Password: `admin123`)
*   **User Demo:** `panji@example.com` (Password: `User@12345`)

---

## 🛠️ *Troubleshooting* (Pemecahan Masalah)

**1. Jika Login Gagal dengan Keterangan "Incorrect Password":**
Jika Anda sudah pernah meng-import database *sebelum* merubah enkripsi password, maka Anda harus meresetnya ke password *plain-text*.
*   Buka tab **SQL** di phpMyAdmin.
*   Jalankan kode ini:
    ```sql
    UPDATE users SET password = 'admin123' WHERE email = 'admin@visualgallery.com';
    UPDATE users SET password = 'User@12345' WHERE username IN ('panji_setia', 'luna_art', 'rafi_foto', 'sari_visual');
    ```
*   Klik **Go**.

**2. Jika Terjadi Error saat Import (Database Already Exists dll):**
File `schema.sql` sudah dirancang aman dengan perintah `CREATE TABLE IF NOT EXISTS` dan `ON DUPLICATE KEY UPDATE`. Jika Anda ingin me-reset total dari nol:
*   Di tab **SQL**, jalankan perintah: `DROP DATABASE visual_gallery_db;`
*   Ulangi panduan dari **Tahap 3**.

🎉 **Selesai! Database Anda sekarang sudah siap, dan Anda bisa me-Run file `Main.java` dari IDE Anda.**
