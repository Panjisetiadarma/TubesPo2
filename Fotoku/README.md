# FixView - Aplikasi Media Sosial Berbagi Foto (Instagram Clone)

Aplikasi desktop "FixView" adalah klon dari antarmuka Instagram Desktop dalam mode gelap (Dark Mode). Aplikasi ini dibangun murni menggunakan **Java SE** dan **Java Swing** tanpa menggunakan framework GUI tambahan (seperti JavaFX).

Proyek ini menerapkan konsep Full Object Oriented Programming (OOP) dan menggunakan arsitektur MVC (Model-View-Controller).

## Fitur Utama
1. **Sidebar Navigasi**: Menu navigasi yang mirip dengan Instagram (Beranda, Cari, Eksplorasi, Reels, dll) menggunakan ikon Unicode.
2. **Stories Horizontal**: Menampilkan stories pengguna yang dapat di-scroll secara horizontal, lengkap dengan gradasi *border* cerita.
3. **Feed Vertikal**: Postingan berbentuk *card* dengan desain responsif.
4. **Tombol Interaktif**: Anda dapat melakukan interaksi pada ikon hati (Like) untuk melihat penambahan jumlah likes, serta dapat menandai ikon *Save*.
5. **UI Dark Mode**: Tampilan UI dikustomisasi secara manual sehingga mendukung *corner radius* membulat pada setiap *card* (*RoundedPanel*), gambar profil bulat (*CircleImagePanel*), dan skema warna gelap *Dark Mode*.

## Persyaratan (Requirements)
- **Java Development Kit (JDK) 8 atau lebih baru.** (Direkomendasikan JDK 11 atau ke atas).
- Command Prompt (Windows) / Terminal (Mac/Linux).

## Cara Menjalankan Aplikasi (Lewat Terminal / CMD)

Karena aplikasi memiliki banyak *package* (`model`, `view`, `controller`, dll), file-file Java tidak bisa dijalankan dengan sekali run `java file.java`. Anda perlu melakukan *compile* ke dalam satu folder output terlebih dahulu.

Ikuti langkah-langkah berikut:

### 1. Buka Terminal dan Masuk ke Direktori Proyek
Buka terminal Anda (CMD / PowerShell / Terminal VS Code) dan arahkan masuk ke folder utama proyek `Fotoku`:
```bash
cd "d:\tugas kuliah\code s4\TubesPo2\Fotoku"
```
*(Atau jika Anda sudah berada di folder `TubesPo2`, cukup jalankan `cd Fotoku`)*

### 2. Kompilasi (Compile) Semua File Kode
Kompilasi semua *source code* (dari dalam folder `src`) ke dalam sebuah folder baru bernama `bin` menggunakan perintah berikut:
```bash
javac -d bin src\utils\*.java src\component\*.java src\model\*.java src\controller\*.java src\view\*.java src\Main.java
```
*(Catatan: Pastikan perintah di atas tidak memunculkan pesan error di terminal. Flag `-d bin` akan otomatis menempatkan hasil kompilasi ber-ekstensi `.class` ke dalam folder bernama `bin`)*

### 3. Jalankan Aplikasi (Run)
Setelah proses kompilasi selesai, panggil dan jalankan kelas `Main` sebagai titik masuk (*entry point*) dari dalam folder `bin`:
```bash
java -cp bin Main
```

Jendela aplikasi antarmuka "Fotoku" akan otomatis terbuka.

---

## Catatan Alternatif: Menjalankan via IDE (VS Code / IntelliJ / Eclipse)
Jika Anda menggunakan IDE, tidak perlu mengetik perintah terminal di atas secara manual:
1. Pastikan folder **`Fotoku`** dibuka sebagai *Root Workspace* (buka folder `Fotoku` langsung di VS Code/IDE, bukan folder `TubesPo2`).
2. Cari dan buka file `src/Main.java`.
3. Klik tombol **Run** atau **Play** (▶️) di pojok kanan atas layar IDE Anda.
