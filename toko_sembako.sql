-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 22, 2021 at 10:42 AM
-- Server version: 10.4.6-MariaDB
-- PHP Version: 7.3.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `toko_sembako`
--

-- --------------------------------------------------------

--
-- Table structure for table `databarang`
--

CREATE TABLE `databarang` (
  `kode_barang` varchar(10) NOT NULL,
  `nama_barang` varchar(50) NOT NULL,
  `satuan` varchar(10) NOT NULL,
  `id_supplier` varchar(10) NOT NULL,
  `stok` int(50) NOT NULL,
  `harga` int(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `databarang`
--

INSERT INTO `databarang` (`kode_barang`, `nama_barang`, `satuan`, `id_supplier`, `stok`, `harga`) VALUES
('B0001', 'Beras Rojo Lele', 'Kilogram', 'S001', 20, 9000),
('B0002', 'Teh Sari Wangi', 'Pack', 'S001', 20, 10000),
('B0003', 'Kopi Kapal Api', 'Sachet', 'S002', 10, 1000),
('B0004', 'Minyak Goreng', 'Liter', 'S002', 10, 5000);

-- --------------------------------------------------------

--
-- Table structure for table `datasupplier`
--

CREATE TABLE `datasupplier` (
  `id_supplier` varchar(10) NOT NULL,
  `nama_supplier` varchar(30) NOT NULL,
  `no_telepon` varchar(18) NOT NULL,
  `alamat` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `datasupplier`
--

INSERT INTO `datasupplier` (`id_supplier`, `nama_supplier`, `no_telepon`, `alamat`) VALUES
('S001', 'PT SRC makmur', '089423457845', 'Jakarta'),
('S002', 'PT Untung Jaya', '088423553845', 'Tangerang');

-- --------------------------------------------------------

--
-- Table structure for table `detail_trans_penjualan`
--

CREATE TABLE `detail_trans_penjualan` (
  `id_detail` int(10) NOT NULL,
  `kode_detail_transaksi` varchar(10) NOT NULL,
  `kode_barang` varchar(10) NOT NULL,
  `harga` int(11) NOT NULL,
  `jumlah` int(11) NOT NULL,
  `discount` int(11) DEFAULT NULL,
  `subtotal` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `detail_trans_penjualan`
--

INSERT INTO `detail_trans_penjualan` (`id_detail`, `kode_detail_transaksi`, `kode_barang`, `harga`, `jumlah`, `discount`, `subtotal`) VALUES
(1, 'DTRX001', 'B0002', 10000, 1, 0, 10000),
(2, 'DTRX002', 'B0001', 9000, 5, 0, 45000),
(3, 'DTRX003', 'B0002', 10000, 1, 0, 10000),
(4, 'DTRX003', 'B0001', 9000, 5, 0, 45000),
(5, 'DTRX003', 'B0003', 1000, 10, 0, 10000),
(11, 'DTRX004', 'B0003', 1000, 5, 0, 5000),
(12, 'DTRX005', 'B0004', 5000, 2, 0, 10000),
(13, 'DTRX006', 'B0001', 9000, 2, 0, 18000),
(14, 'DTRX006', 'B0004', 5000, 1, 0, 5000),
(15, 'DTRX006', 'B0002', 10000, 1, 0, 10000),
(19, 'DTRX007', 'B0004', 5000, 1, 0, 5000),
(20, 'DTRX008', 'B0001', 9000, 2, 0, 18000),
(21, 'DTRX008', 'B0004', 5000, 5, 0, 25000);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi_penjualan`
--

CREATE TABLE `transaksi_penjualan` (
  `kode_transaksi` varchar(10) NOT NULL,
  `kode_detail_transaksi` varchar(10) NOT NULL,
  `id_username` varchar(10) NOT NULL,
  `tanggal` date NOT NULL,
  `jam` time NOT NULL,
  `total_transaksi` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaksi_penjualan`
--

INSERT INTO `transaksi_penjualan` (`kode_transaksi`, `kode_detail_transaksi`, `id_username`, `tanggal`, `jam`, `total_transaksi`) VALUES
('TRX001', 'DTRX001', 'Admin01', '2021-04-09', '23:40:15', 10000),
('TRX002', 'DTRX002', 'Admin01', '2021-04-12', '01:23:28', 45000),
('TRX003', 'DTRX003', 'Admin01', '2021-04-12', '01:25:30', 65000),
('TRX004', 'DTRX004', 'Admin01', '2021-06-07', '12:46:44', 5000),
('TRX005', 'DTRX005', 'Admin01', '2021-06-14', '13:13:19', 10000),
('TRX006', 'DTRX006', 'Admin01', '2021-06-17', '11:27:13', 33000),
('TRX007', 'DTRX007', 'Admin01', '2021-06-17', '11:36:52', 5000),
('TRX008', 'DTRX008', 'Admin01', '2021-06-19', '09:26:24', 43000);

-- --------------------------------------------------------

--
-- Table structure for table `username_login`
--

CREATE TABLE `username_login` (
  `id_username` varchar(10) NOT NULL,
  `Nama` varchar(50) NOT NULL,
  `jabatan` varchar(20) NOT NULL,
  `username` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `username_login`
--

INSERT INTO `username_login` (`id_username`, `Nama`, `jabatan`, `username`, `password`) VALUES
('Admin01', 'Abdullah', 'Pemilik Toko', 'admin', 'admin');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `databarang`
--
ALTER TABLE `databarang`
  ADD PRIMARY KEY (`kode_barang`),
  ADD KEY `id_supplier` (`id_supplier`);

--
-- Indexes for table `datasupplier`
--
ALTER TABLE `datasupplier`
  ADD PRIMARY KEY (`id_supplier`);

--
-- Indexes for table `detail_trans_penjualan`
--
ALTER TABLE `detail_trans_penjualan`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `kode_barang` (`kode_barang`),
  ADD KEY `kode_detail_transaksi` (`kode_detail_transaksi`);

--
-- Indexes for table `transaksi_penjualan`
--
ALTER TABLE `transaksi_penjualan`
  ADD PRIMARY KEY (`kode_transaksi`),
  ADD UNIQUE KEY `kode_detail_transaksi` (`kode_detail_transaksi`),
  ADD KEY `id_username` (`id_username`);

--
-- Indexes for table `username_login`
--
ALTER TABLE `username_login`
  ADD PRIMARY KEY (`id_username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `detail_trans_penjualan`
--
ALTER TABLE `detail_trans_penjualan`
  MODIFY `id_detail` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `databarang`
--
ALTER TABLE `databarang`
  ADD CONSTRAINT `databarang_ibfk_1` FOREIGN KEY (`id_supplier`) REFERENCES `datasupplier` (`id_supplier`);

--
-- Constraints for table `detail_trans_penjualan`
--
ALTER TABLE `detail_trans_penjualan`
  ADD CONSTRAINT `detail_trans_penjualan_ibfk_1` FOREIGN KEY (`kode_barang`) REFERENCES `databarang` (`kode_barang`),
  ADD CONSTRAINT `detail_trans_penjualan_ibfk_2` FOREIGN KEY (`kode_detail_transaksi`) REFERENCES `transaksi_penjualan` (`kode_detail_transaksi`);

--
-- Constraints for table `transaksi_penjualan`
--
ALTER TABLE `transaksi_penjualan`
  ADD CONSTRAINT `transaksi_penjualan_ibfk_1` FOREIGN KEY (`id_username`) REFERENCES `username_login` (`id_username`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
