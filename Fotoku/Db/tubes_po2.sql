-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 22 Jun 2026 pada 19.05
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tubes_po2`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `comments`
--

CREATE TABLE `comments` (
  `id_comment` int(11) NOT NULL,
  `id_post` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `parent_comment_id` int(11) DEFAULT NULL,
  `comment_text` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `comments`
--

INSERT INTO `comments` (`id_comment`, `id_post`, `id_user`, `parent_comment_id`, `comment_text`, `created_at`) VALUES
(1, 1, 3, NULL, 'hai', '2026-06-20 12:46:47'),
(2, 1, 3, 1, 'apa', '2026-06-20 12:50:46'),
(3, 1, 3, NULL, 'tes', '2026-06-20 12:50:50'),
(9, 5, 3, NULL, 'hai', '2026-06-21 12:20:57'),
(10, 5, 3, NULL, 'mabar', '2026-06-21 13:02:57'),
(12, 5, 3, NULL, 'hai', '2026-06-21 14:14:29');

-- --------------------------------------------------------

--
-- Struktur dari tabel `comment_likes`
--

CREATE TABLE `comment_likes` (
  `id_comment_like` int(11) NOT NULL,
  `id_comment` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `comment_likes`
--

INSERT INTO `comment_likes` (`id_comment_like`, `id_comment`, `id_user`, `created_at`) VALUES
(2, 1, 3, '2026-06-20 12:50:33');

-- --------------------------------------------------------

--
-- Struktur dari tabel `likes`
--

CREATE TABLE `likes` (
  `id_like` int(11) NOT NULL,
  `id_post` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `likes`
--

INSERT INTO `likes` (`id_like`, `id_post`, `id_user`, `created_at`) VALUES
(3, 1, 3, '2026-06-20 13:13:14'),
(13, 5, 3, '2026-06-21 13:03:34'),
(14, 8, 3, '2026-06-21 13:19:33'),
(15, 8, 4, '2026-06-22 15:35:32');

-- --------------------------------------------------------

--
-- Struktur dari tabel `posts`
--

CREATE TABLE `posts` (
  `id_post` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `file_type` enum('image','video') NOT NULL,
  `caption` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `posts`
--

INSERT INTO `posts` (`id_post`, `id_user`, `file_path`, `file_type`, `caption`, `created_at`) VALUES
(1, 3, 'C:/Users/HP/Pictures/Camera Roll/WIN_20250605_12_07_02_Pro.jpg', 'image', 'haii ganteng', '2026-06-20 12:38:39'),
(5, 3, 'C:\\Users\\HP\\Pictures\\gambar.jpg', 'image', 'tes', '2026-06-21 12:15:17'),
(8, 3, 'C:\\Users\\HP\\Pictures\\fotro latar merah.jpg', 'image', 'TES', '2026-06-21 12:32:10'),
(9, 3, 'C:\\Users\\HP\\Pictures\\gambar.jpg', 'image', 'TES', '2026-06-21 12:32:10'),
(10, 3, 'C:\\Users\\HP\\Pictures\\ikan.jpg', 'image', 'TES', '2026-06-21 12:32:10'),
(11, 3, 'C:\\Users\\HP\\Pictures\\mu.jpg', 'image', 'TES', '2026-06-21 12:32:10'),
(13, 3, 'C:\\Users\\HP\\Pictures\\febrian.png', 'image', 'tes', '2026-06-22 15:50:37'),
(14, 4, 'C:\\Users\\HP\\Pictures\\backup7plus\\2025_10_18_21_29_IMG_0325.PNG', 'image', 'tes', '2026-06-22 15:56:47');

-- --------------------------------------------------------

--
-- Struktur dari tabel `saves`
--

CREATE TABLE `saves` (
  `id_save` int(11) NOT NULL,
  `id_post` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `saves`
--

INSERT INTO `saves` (`id_save`, `id_post`, `id_user`, `created_at`) VALUES
(2, 1, 3, '2026-06-20 12:48:09'),
(5, 5, 3, '2026-06-21 12:31:23');

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `bio` text DEFAULT NULL,
  `avatar_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id_user`, `username`, `email`, `password`, `created_at`, `bio`, `avatar_path`) VALUES
(1, 'pepvri', 'feb@gmail.com', '123', '2026-06-10 17:34:46', NULL, NULL),
(2, 'Ujiw', 'Ujiw@gmail.com', '12345678', '2026-06-11 13:13:44', NULL, NULL),
(3, 'febrian', 'kailu@gmail.com', 'pep12345', '2026-06-20 12:34:07', 'Peace', 'avatar_3_1782141218652.jpg'),
(4, 'raziq', 'ziq@gmail.com', 'kailu123', '2026-06-22 14:48:46', NULL, NULL);

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id_comment`),
  ADD KEY `id_post` (`id_post`),
  ADD KEY `id_user` (`id_user`),
  ADD KEY `parent_comment_id` (`parent_comment_id`);

--
-- Indeks untuk tabel `comment_likes`
--
ALTER TABLE `comment_likes`
  ADD PRIMARY KEY (`id_comment_like`),
  ADD KEY `id_comment` (`id_comment`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `likes`
--
ALTER TABLE `likes`
  ADD PRIMARY KEY (`id_like`),
  ADD UNIQUE KEY `unique_like` (`id_post`,`id_user`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id_post`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `saves`
--
ALTER TABLE `saves`
  ADD PRIMARY KEY (`id_save`),
  ADD KEY `id_post` (`id_post`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `comments`
--
ALTER TABLE `comments`
  MODIFY `id_comment` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT untuk tabel `comment_likes`
--
ALTER TABLE `comment_likes`
  MODIFY `id_comment_like` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `likes`
--
ALTER TABLE `likes`
  MODIFY `id_like` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT untuk tabel `posts`
--
ALTER TABLE `posts`
  MODIFY `id_post` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT untuk tabel `saves`
--
ALTER TABLE `saves`
  MODIFY `id_save` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`) ON DELETE CASCADE,
  ADD CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `comments_ibfk_3` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`id_comment`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `comment_likes`
--
ALTER TABLE `comment_likes`
  ADD CONSTRAINT `comment_likes_ibfk_1` FOREIGN KEY (`id_comment`) REFERENCES `comments` (`id_comment`) ON DELETE CASCADE,
  ADD CONSTRAINT `comment_likes_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `likes`
--
ALTER TABLE `likes`
  ADD CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`) ON DELETE CASCADE,
  ADD CONSTRAINT `likes_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `posts`
--
ALTER TABLE `posts`
  ADD CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `saves`
--
ALTER TABLE `saves`
  ADD CONSTRAINT `saves_ibfk_1` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`) ON DELETE CASCADE,
  ADD CONSTRAINT `saves_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
