/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toko_sembako;

import java.awt.*;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.text.*;
import java.util.logging.*;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Fadhilansyah25
 */
public class Dashboard extends javax.swing.JFrame {
    Connection con;
    Statement stat;
    PreparedStatement pst;
    ResultSet res;
    String id_barang, id_supplier;
    // Deklarasi variabel penting Transaksi
    String kode_barang;
    int harga_barang, stok_barang, sub_total, diskon;
    DefaultTableModel modelList;

    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        con = database.koneksiDB();
        // Method Panel Barang
        loadTabel_barang();
        kosongkan_formBarang();
        comboBox_formbarang();

        // Method Panel Supplier
        loadTabel_supplier();
        kosongkan_formSupplier();

        // Method Panel Transaksi
        delay();
        autonumber();
        // Tabel ListCart
        modelList = new DefaultTableModel();
        listCartBarang_jTable.setModel(modelList);
        modelList.addColumn("Kode Detail");
        modelList.addColumn("Kode Barang");
        modelList.addColumn("Harga");
        modelList.addColumn("Jumlah");
        modelList.addColumn("Diskon");
        modelList.addColumn("Sub Total");
        sum();

        // Method Panel Laporan Penjualan
        load_tabelPenjualan();
    }

    // ===============================================================================================

    // Method Panel data barang
    public final void loadTabel_barang() {
        DefaultTableModel tabel = new DefaultTableModel();
        tabel.addColumn("kode barang");
        tabel.addColumn("Nama Barang");
        tabel.addColumn("Satuan");
        tabel.addColumn("Kode Supplier");
        tabel.addColumn("Supplier");
        tabel.addColumn("Stok Barang");
        tabel.addColumn("Harga Barang");
        tabel_barang.setModel(tabel);

        try {
            String sql = "Select * From databarang inner join datasupplier on databarang.id_supplier=datasupplier.id_supplier";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();
            while (res.next()) {
                tabel.addRow(new Object[] { res.getString("kode_barang"), res.getString("nama_barang"),
                        res.getString("satuan"), res.getString("id_supplier"), res.getString("nama_supplier"),
                        res.getString("stok"), res.getString("harga") });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void kosongkan_formBarang() {
        JTkode_barang.setText("");
        JTnama_barang.setText("");
        JTsatuan_barang.setText("");
        CBnamaSupplier.setSelectedIndex(-1);
        JTsupplier_barang.setText("");
        JTstok_barang.setText("");
        JTharga_barang.setText("");
    }

    private void comboBox_formbarang() {
        try {
            String sql = "select nama_supplier from datasupplier order by nama_supplier asc";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();
            CBnamaSupplier.removeAllItems();
            while (res.next()) {
                CBnamaSupplier.addItem(res.getString("nama_supplier"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void idSupplier_barang() {
        try {
            String sql = "select id_supplier from datasupplier where nama_supplier='" + CBnamaSupplier.getSelectedItem()
                    + "'";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();

            while (res.next()) {
                {
                    JTsupplier_barang.setText(res.getString("id_supplier"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }
    }

    // Method Panel data supplier
    public final void loadTabel_supplier() {
        DefaultTableModel tabel = new DefaultTableModel();
        tabel.addColumn("ID Supplier");
        tabel.addColumn("Nama Supplier");
        tabel.addColumn("No. Telepon");
        tabel.addColumn("Alamat");
        tabel_supplier.setModel(tabel);

        try {
            pst = con.prepareStatement("Select * From datasupplier");
            res = pst.executeQuery();
            while (res.next()) {
                tabel.addRow(new Object[] { res.getString("id_supplier"), res.getString("nama_supplier"),
                        res.getString("no_telepon"), res.getString("alamat") });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void kosongkan_formSupplier() {
        JTidSupplier_supplier.setText("");
        JTnama_supplier.setText("");
        JTnomor_supplier.setText("");
        JTalamat_supplier.setText("");
    }

    // Method Panel Transaksi
    public final void delay() {
        Thread clock = new Thread() {
            public void run() {
                for (;;) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                    Jam_jTextField.setText(format.format(cal.getTime()));
                    Tanggal_jTextField1.setText(format2.format(cal.getTime()));

                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        clock.start();
    }

    public void cari_barang() {
        try {
            String sql = "select * from databarang where nama_barang LIKE '%" + cariBarang_jTextField.getText() + "%'";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();
            hasilCariBarang_jTable.setModel(DbUtils.resultSetToTableModel(res));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void autonumber() {
        try {
            String sql = "SELECT MAX(RIGHT(Kode_Transaksi,3)) AS NO FROM transaksi_penjualan";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();
            while (res.next()) {
                if (res.first() == false) {
                    kodeTransaki_jTextField.setText("TRX001");
                } else {
                    res.last();
                    int auto_id = res.getInt(1) + 1;
                    String no = String.valueOf(auto_id);
                    int NomorJual = no.length();
                    for (int j = 0; j < 3 - NomorJual; j++) {
                        no = "0" + no;
                    }
                    kodeTransaki_jTextField.setText("TRX" + no);
                }
            }
            res.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void subtotal() {
        int jumlah;
        if (discountBarang_jTextField.getText().equals("")) {
            diskon = 0;
        } else {
            diskon = Integer.parseInt(discountBarang_jTextField.getSelectedText());
        }
        jumlah = Integer.parseInt(JumlahBarang_jTextField.getText());
        sub_total = (jumlah * harga_barang) - diskon;
    }

    public void sum() {
        int totalBiaya = 0;
        int subtotal;
        DefaultTableModel dataModel = (DefaultTableModel) listCartBarang_jTable.getModel();
        int jumlah = listCartBarang_jTable.getRowCount();
        for (int i = 0; i < jumlah; i++) {
            subtotal = Integer.parseInt(dataModel.getValueAt(i, 5).toString());
            totalBiaya += subtotal;
        }
        totalBiaya_jTextField.setText(String.valueOf(totalBiaya));
    }

    public void simpan_transaksi() {
        String tgl = Tanggal_jTextField1.getText();
        String jam = Jam_jTextField.getText();
        String kode_detail = "D" + kodeTransaki_jTextField.getText();
        String id_username = username_jLabel.getText();

        try {
            String sql = "insert into transaksi_penjualan (Kode_Transaksi, Kode_Detail_transaksi, id_username, tanggal, jam, total_transaksi) value (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, kodeTransaki_jTextField.getText());
            pst.setString(2, kode_detail);
            pst.setString(3, id_username);
            pst.setString(4, tgl);
            pst.setString(5, jam);
            pst.setString(6, totalBiaya_jTextField.getText());
            pst.execute();
            JOptionPane.showMessageDialog(null, "Data Tersimpan");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void simpan_detailTransaksi() {
        try {
            DefaultTableModel dataModel = (DefaultTableModel) listCartBarang_jTable.getModel();
            int rows = listCartBarang_jTable.getRowCount();
            con.setAutoCommit(false);

            String sql = "Insert Into detail_trans_penjualan(kode_detail_transaksi, kode_barang, harga, jumlah, discount, subtotal) values (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sql);
            for (int row = 0; row < rows; row++) {
                String kode_detail = (String) dataModel.getValueAt(row, 0);
                String kode_barang = (String) dataModel.getValueAt(row, 1);
                int harga = Integer.parseInt(dataModel.getValueAt(row, 2).toString());
                int jumlah = Integer.parseInt(dataModel.getValueAt(row, 3).toString());
                int discount = Integer.parseInt(dataModel.getValueAt(row, 4).toString());
                int subTotal = Integer.parseInt(dataModel.getValueAt(row, 5).toString());
                pst.setString(1, kode_detail);
                pst.setString(2, kode_barang);
                pst.setInt(3, harga);
                pst.setInt(4, jumlah);
                pst.setInt(5, discount);
                pst.setInt(6, subTotal);

                pst.addBatch();
            }
            pst.executeBatch();
            con.commit();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Method panel laporan penjualan
    public void load_tabelPenjualan() {
        try {
            String sql = "select * from transaksi_penjualan";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();
            dataTransaksi_jTable.setModel(DbUtils.resultSetToTableModel(res));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // ===============================================================================================

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        sidePanel = new javax.swing.JPanel();
        dataBarang_tabBut = new javax.swing.JPanel();
        dataBarang_tg = new javax.swing.JLabel();
        dataSupplier_tabBut = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        transaksi_tabBut = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        dataPenjualan_tabBut = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        user_panel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        logout_label = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        panelBarang = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        JTkode_barang = new javax.swing.JTextField();
        JTnama_barang = new javax.swing.JTextField();
        JTsatuan_barang = new javax.swing.JTextField();
        JTsupplier_barang = new javax.swing.JTextField();
        JTstok_barang = new javax.swing.JTextField();
        JTharga_barang = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        BTinsert_barang = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_barang = new javax.swing.JTable();
        BTedit_barang = new javax.swing.JButton();
        BTdelete_barang = new javax.swing.JButton();
        CBnamaSupplier = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        BTrefresh_barang = new javax.swing.JButton();
        panelSupplier = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_supplier = new javax.swing.JTable();
        JTidSupplier_supplier = new javax.swing.JTextField();
        JTnama_supplier = new javax.swing.JTextField();
        JTnomor_supplier = new javax.swing.JTextField();
        JTalamat_supplier = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        BTadd_supplier = new javax.swing.JButton();
        BTedit_supplier = new javax.swing.JButton();
        BTdelete_supplier = new javax.swing.JButton();
        BTrefresh_supplier = new javax.swing.JButton();
        panelTransasksi = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cariBarang_jTextField = new javax.swing.JTextField();
        cariBarang_jButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        hasilCariBarang_jTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        listCartBarang_jTable = new javax.swing.JTable();
        JumlahBarang_jTextField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        discountBarang_jTextField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tambahCart_jButton = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        kodeTransaki_jTextField = new javax.swing.JTextField();
        kurangiCart_jButton = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        totalBiaya_jTextField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        bayarTrans_jTextField = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        kembalian_jTextField = new javax.swing.JTextField();
        simpanTrans_jButton = new javax.swing.JButton();
        transBaru_jButton = new javax.swing.JButton();
        Jam_jTextField = new javax.swing.JTextField();
        Tanggal_jTextField1 = new javax.swing.JTextField();
        panelLaporanTr = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jScrollPane5 = new javax.swing.JScrollPane();
        dataTransaksi_jTable = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        detailTransaksi_jTable = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard");
        setLocation(new java.awt.Point(0, 0));
        setResizable(false);
        setSize(new java.awt.Dimension(0, 0));

        sidePanel.setBackground(new java.awt.Color(255, 51, 51));
        sidePanel.setPreferredSize(new java.awt.Dimension(250, 600));

        dataBarang_tabBut.setBackground(new java.awt.Color(255, 153, 0));
        dataBarang_tabBut.setPreferredSize(new java.awt.Dimension(200, 50));
        dataBarang_tabBut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataBarang_tabButMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dataBarang_tabButMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dataBarang_tabButMouseExited(evt);
            }
        });

        dataBarang_tg.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        dataBarang_tg.setForeground(new java.awt.Color(255, 255, 255));
        dataBarang_tg.setText("DATA BARANG");

        javax.swing.GroupLayout dataBarang_tabButLayout = new javax.swing.GroupLayout(dataBarang_tabBut);
        dataBarang_tabBut.setLayout(dataBarang_tabButLayout);
        dataBarang_tabButLayout.setHorizontalGroup(
            dataBarang_tabButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataBarang_tabButLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(dataBarang_tg)
                .addContainerGap(118, Short.MAX_VALUE))
        );
        dataBarang_tabButLayout.setVerticalGroup(
            dataBarang_tabButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dataBarang_tabButLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dataBarang_tg, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addContainerGap())
        );

        dataSupplier_tabBut.setBackground(new java.awt.Color(255, 153, 0));
        dataSupplier_tabBut.setPreferredSize(new java.awt.Dimension(200, 50));
        dataSupplier_tabBut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataSupplier_tabButMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dataSupplier_tabButMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dataSupplier_tabButMouseExited(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("DATA SUPPLIER");

        javax.swing.GroupLayout dataSupplier_tabButLayout = new javax.swing.GroupLayout(dataSupplier_tabBut);
        dataSupplier_tabBut.setLayout(dataSupplier_tabButLayout);
        dataSupplier_tabButLayout.setHorizontalGroup(
            dataSupplier_tabButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataSupplier_tabButLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dataSupplier_tabButLayout.setVerticalGroup(
            dataSupplier_tabButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dataSupplier_tabButLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        transaksi_tabBut.setBackground(new java.awt.Color(255, 153, 0));
        transaksi_tabBut.setPreferredSize(new java.awt.Dimension(200, 50));
        transaksi_tabBut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                transaksi_tabButMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                transaksi_tabButMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                transaksi_tabButMouseExited(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("TRANSAKSI");

        javax.swing.GroupLayout transaksi_tabButLayout = new javax.swing.GroupLayout(transaksi_tabBut);
        transaksi_tabBut.setLayout(transaksi_tabButLayout);
        transaksi_tabButLayout.setHorizontalGroup(
            transaksi_tabButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transaksi_tabButLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        transaksi_tabButLayout.setVerticalGroup(
            transaksi_tabButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transaksi_tabButLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        dataPenjualan_tabBut.setBackground(new java.awt.Color(255, 153, 0));
        dataPenjualan_tabBut.setPreferredSize(new java.awt.Dimension(200, 50));
        dataPenjualan_tabBut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataPenjualan_tabButMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dataPenjualan_tabButMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dataPenjualan_tabButMouseExited(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("DATA PENJUALAN");

        javax.swing.GroupLayout dataPenjualan_tabButLayout = new javax.swing.GroupLayout(dataPenjualan_tabBut);
        dataPenjualan_tabBut.setLayout(dataPenjualan_tabButLayout);
        dataPenjualan_tabButLayout.setHorizontalGroup(
            dataPenjualan_tabButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPenjualan_tabButLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel23)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dataPenjualan_tabButLayout.setVerticalGroup(
            dataPenjualan_tabButLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dataPenjualan_tabButLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        user_panel.setBackground(new java.awt.Color(255, 102, 102));

        username_jLabel.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        username_jLabel.setForeground(new java.awt.Color(255, 255, 255));
        username_jLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        username_jLabel.setText("Username");

        namaUser_jLabel1.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        namaUser_jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        namaUser_jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        namaUser_jLabel1.setText("Nama");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("ID User     :");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Nama         :");

        javax.swing.GroupLayout user_panelLayout = new javax.swing.GroupLayout(user_panel);
        user_panel.setLayout(user_panelLayout);
        user_panelLayout.setHorizontalGroup(
            user_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(user_panelLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(user_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(user_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(username_jLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(namaUser_jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        user_panelLayout.setVerticalGroup(
            user_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, user_panelLayout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addGroup(user_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(namaUser_jLabel1))
                .addGap(18, 18, 18)
                .addGroup(user_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(username_jLabel))
                .addGap(43, 43, 43))
        );

        logout_label.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        logout_label.setForeground(new java.awt.Color(255, 255, 255));
        logout_label.setText("Log out");
        logout_label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logout_labelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logout_labelMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout sidePanelLayout = new javax.swing.GroupLayout(sidePanel);
        sidePanel.setLayout(sidePanelLayout);
        sidePanelLayout.setHorizontalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dataSupplier_tabBut, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
            .addComponent(transaksi_tabBut, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(logout_label)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dataBarang_tabBut, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(user_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(dataPenjualan_tabBut, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );
        sidePanelLayout.setVerticalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addComponent(user_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(dataBarang_tabBut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(dataSupplier_tabBut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(transaksi_tabBut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(dataPenjualan_tabBut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logout_label)
                .addGap(26, 26, 26))
        );

        mainPanel.setLayout(new java.awt.CardLayout());

        panelBarang.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel1.setText("DATA BARANG");

        JTsupplier_barang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JTsupplier_barangMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel5.setText("Kode Barang");

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel6.setText("Nama Barang");

        jLabel7.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel7.setText("Satuan");

        jLabel8.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel8.setText("ID Supplier");

        jLabel9.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel9.setText("Stok");

        jLabel10.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel10.setText("Harga Satuan");

        BTinsert_barang.setText("Add Data");
        BTinsert_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTinsert_barangActionPerformed(evt);
            }
        });

        tabel_barang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_barang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_barangMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabel_barang);

        BTedit_barang.setText("Edit Data");
        BTedit_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTedit_barangActionPerformed(evt);
            }
        });

        BTdelete_barang.setText("Delete Data");
        BTdelete_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTdelete_barangActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel11.setText("Nama Supplier");

        BTrefresh_barang.setText("Refresh All");
        BTrefresh_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTrefresh_barangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBarangLayout = new javax.swing.GroupLayout(panelBarang);
        panelBarang.setLayout(panelBarangLayout);
        panelBarangLayout.setHorizontalGroup(
            panelBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBarangLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBarangLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelBarangLayout.createSequentialGroup()
                        .addGroup(panelBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(JTharga_barang)
                                .addComponent(JTstok_barang)
                                .addComponent(JTsupplier_barang)
                                .addComponent(JTsatuan_barang)
                                .addComponent(JTnama_barang)
                                .addComponent(JTkode_barang, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7)
                                .addComponent(jLabel8)
                                .addComponent(jLabel10)
                                .addComponent(jLabel9)
                                .addComponent(CBnamaSupplier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panelBarangLayout.createSequentialGroup()
                                    .addComponent(BTinsert_barang)
                                    .addGap(18, 18, 18)
                                    .addGroup(panelBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(BTrefresh_barang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(BTedit_barang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(18, 18, 18)
                                    .addComponent(BTdelete_barang)))
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))))
        );

        panelBarangLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BTdelete_barang, BTedit_barang, BTinsert_barang});

        panelBarangLayout.setVerticalGroup(
            panelBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBarangLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addGap(43, 43, 43)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelBarangLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(panelBarangLayout.createSequentialGroup()
                        .addComponent(JTkode_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTnama_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTsatuan_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CBnamaSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTsupplier_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTstok_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTharga_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)))
                .addGroup(panelBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BTinsert_barang)
                    .addComponent(BTedit_barang)
                    .addComponent(BTdelete_barang))
                .addGap(31, 31, 31)
                .addComponent(BTrefresh_barang)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        mainPanel.add(panelBarang, "card2");

        panelSupplier.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel2.setText("DATA SUPPLIER");

        tabel_supplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_supplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_supplierMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel_supplier);

        jLabel12.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel12.setText("ID Supplier");

        jLabel13.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel13.setText("Nama Supplier");

        jLabel14.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel14.setText("No. Telepon");

        jLabel15.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jLabel15.setText("Alamat");

        BTadd_supplier.setText("Add Data");
        BTadd_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTadd_supplierActionPerformed(evt);
            }
        });

        BTedit_supplier.setText("Edit Data");
        BTedit_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTedit_supplierActionPerformed(evt);
            }
        });

        BTdelete_supplier.setText("Delete Data");
        BTdelete_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTdelete_supplierActionPerformed(evt);
            }
        });

        BTrefresh_supplier.setText("Refresh All");
        BTrefresh_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTrefresh_supplierActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSupplierLayout = new javax.swing.GroupLayout(panelSupplier);
        panelSupplier.setLayout(panelSupplierLayout);
        panelSupplierLayout.setHorizontalGroup(
            panelSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSupplierLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSupplierLayout.createSequentialGroup()
                        .addGroup(panelSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JTidSupplier_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JTnama_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JTnomor_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(JTalamat_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addGroup(panelSupplierLayout.createSequentialGroup()
                                .addGroup(panelSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(BTrefresh_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panelSupplierLayout.createSequentialGroup()
                                        .addComponent(BTadd_supplier)
                                        .addGap(18, 18, 18)
                                        .addComponent(BTedit_supplier)))
                                .addGap(18, 18, 18)
                                .addComponent(BTdelete_supplier)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))
                    .addGroup(panelSupplierLayout.createSequentialGroup()
                        .addGroup(panelSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel2))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        panelSupplierLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BTadd_supplier, BTdelete_supplier, BTedit_supplier});

        panelSupplierLayout.setVerticalGroup(
            panelSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSupplierLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel2)
                .addGap(50, 50, 50)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSupplierLayout.createSequentialGroup()
                        .addComponent(JTidSupplier_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTnama_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTnomor_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JTalamat_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addGroup(panelSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BTadd_supplier)
                            .addComponent(BTedit_supplier)
                            .addComponent(BTdelete_supplier))
                        .addGap(33, 33, 33)
                        .addComponent(BTrefresh_supplier))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(158, Short.MAX_VALUE))
        );

        mainPanel.add(panelSupplier, "card3");

        panelTransasksi.setBackground(new java.awt.Color(255, 255, 255));
        panelTransasksi.setMaximumSize(new java.awt.Dimension(1200, 700));
        panelTransasksi.setMinimumSize(new java.awt.Dimension(1200, 700));

        jLabel18.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel18.setText("FORM TRANSAKSI");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setText("Masukan Nama Barang");

        cariBarang_jTextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cariBarang_jTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        cariBarang_jTextField.setToolTipText("");

        cariBarang_jButton.setText("Cari");
        cariBarang_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariBarang_jButtonActionPerformed(evt);
            }
        });

        hasilCariBarang_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        hasilCariBarang_jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hasilCariBarang_jTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(hasilCariBarang_jTable);

        listCartBarang_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(listCartBarang_jTable);

        jLabel20.setText("Jumlah");

        jLabel21.setText("Discount");

        tambahCart_jButton.setText("Tambah");
        tambahCart_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahCart_jButtonActionPerformed(evt);
            }
        });

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Tambahkan Cart");
        jLabel22.setToolTipText("");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setText("Data Barang");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel25.setText("Kode Transaksi");

        kodeTransaki_jTextField.setEditable(false);
        kodeTransaki_jTextField.setBackground(new java.awt.Color(204, 204, 204));
        kodeTransaki_jTextField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        kodeTransaki_jTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        kodeTransaki_jTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        kurangiCart_jButton.setText("Hapus");
        kurangiCart_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kurangiCart_jButtonActionPerformed(evt);
            }
        });

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Kurangi Cart");
        jLabel26.setToolTipText("");

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel27.setText("TOTAL ");

        totalBiaya_jTextField.setEditable(false);
        totalBiaya_jTextField.setBackground(new java.awt.Color(204, 204, 204));
        totalBiaya_jTextField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        totalBiaya_jTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        totalBiaya_jTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel28.setText("BAYAR");

        bayarTrans_jTextField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel29.setText("KEMBALIAN");

        kembalian_jTextField.setEditable(false);
        kembalian_jTextField.setBackground(new java.awt.Color(204, 204, 204));
        kembalian_jTextField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        kembalian_jTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        kembalian_jTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        kembalian_jTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                kembalian_jTextFieldMouseClicked(evt);
            }
        });

        simpanTrans_jButton.setText("BAYAR");
        simpanTrans_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanTrans_jButtonActionPerformed(evt);
            }
        });

        transBaru_jButton.setText("NEW");

        Jam_jTextField.setEditable(false);
        Jam_jTextField.setBackground(new java.awt.Color(255, 51, 51));
        Jam_jTextField.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Jam_jTextField.setForeground(new java.awt.Color(255, 255, 255));
        Jam_jTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Jam_jTextField.setBorder(null);

        Tanggal_jTextField1.setEditable(false);
        Tanggal_jTextField1.setBackground(new java.awt.Color(255, 51, 51));
        Tanggal_jTextField1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Tanggal_jTextField1.setForeground(new java.awt.Color(255, 255, 255));
        Tanggal_jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Tanggal_jTextField1.setBorder(null);

        javax.swing.GroupLayout panelTransasksiLayout = new javax.swing.GroupLayout(panelTransasksi);
        panelTransasksi.setLayout(panelTransasksiLayout);
        panelTransasksiLayout.setHorizontalGroup(
            panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTransasksiLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addGroup(panelTransasksiLayout.createSequentialGroup()
                        .addComponent(cariBarang_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addComponent(cariBarang_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTransasksiLayout.createSequentialGroup()
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(JumlahBarang_jTextField)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                        .addGap(31, 31, 31)
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(discountBarang_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tambahCart_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelTransasksiLayout.createSequentialGroup()
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTransasksiLayout.createSequentialGroup()
                                .addComponent(simpanTrans_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(transBaru_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelTransasksiLayout.createSequentialGroup()
                                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel27))
                                .addGap(27, 27, 27)
                                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(bayarTrans_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalBiaya_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kembalian_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kurangiCart_jButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelTransasksiLayout.createSequentialGroup()
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(Tanggal_jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTransasksiLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(kodeTransaki_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelTransasksiLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Jam_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(47, 47, 47))
        );
        panelTransasksiLayout.setVerticalGroup(
            panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransasksiLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel18)
                .addGap(11, 11, 11)
                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jam_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Tanggal_jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cariBarang_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cariBarang_jButton)
                    .addComponent(jLabel25)
                    .addComponent(kodeTransaki_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTransasksiLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kurangiCart_jButton))
                    .addGroup(panelTransasksiLayout.createSequentialGroup()
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTransasksiLayout.createSequentialGroup()
                                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel22))
                                .addGap(16, 16, 16)
                                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(discountBarang_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(JumlahBarang_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tambahCart_jButton)))
                            .addGroup(panelTransasksiLayout.createSequentialGroup()
                                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(totalBiaya_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel27))
                                .addGap(18, 18, 18)
                                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(bayarTrans_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28))
                                .addGap(18, 18, 18)
                                .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(kembalian_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29)))
                            .addComponent(jLabel20))
                        .addGap(41, 41, 41)
                        .addGroup(panelTransasksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(transBaru_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(simpanTrans_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(43, 52, Short.MAX_VALUE))
        );

        panelTransasksiLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {JumlahBarang_jTextField, discountBarang_jTextField});

        mainPanel.add(panelTransasksi, "card4");

        panelLaporanTr.setBackground(new java.awt.Color(255, 255, 255));

        jLabel30.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel30.setText("LAPORAN PENJUALAN");

        dataTransaksi_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        dataTransaksi_jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataTransaksi_jTableMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(dataTransaksi_jTable);

        detailTransaksi_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(detailTransaksi_jTable);

        jLabel31.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel31.setText("Data Transaksi");

        jLabel32.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel32.setText("Detail Transaksi");

        javax.swing.GroupLayout panelLaporanTrLayout = new javax.swing.GroupLayout(panelLaporanTr);
        panelLaporanTr.setLayout(panelLaporanTrLayout);
        panelLaporanTrLayout.setHorizontalGroup(
            panelLaporanTrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLaporanTrLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelLaporanTrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLaporanTrLayout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelLaporanTrLayout.createSequentialGroup()
                        .addGroup(panelLaporanTrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                        .addGap(753, 753, 753))
                    .addGroup(panelLaporanTrLayout.createSequentialGroup()
                        .addGroup(panelLaporanTrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32)
                            .addComponent(jLabel31))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panelLaporanTrLayout.setVerticalGroup(
            panelLaporanTrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLaporanTrLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel30)
                .addGap(45, 45, 45)
                .addGroup(panelLaporanTrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        mainPanel.add(panelLaporanTr, "card5");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1200, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 701, Short.MAX_VALUE)
        );

        mainPanel.add(jPanel2, "card6");

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(background);
        background.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addComponent(sidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // =========================================================================================================

    // EVENT SIDE PANEL
    // sidepanel data barang
    private void dataBarang_tabButMouseClicked(java.awt.event.MouseEvent evt) {
        panelBarang.setVisible(true);
        panelTransasksi.setVisible(false);
        panelSupplier.setVisible(false);
        panelLaporanTr.setVisible(false);
    }

    private void dataBarang_tabButMouseEntered(java.awt.event.MouseEvent evt) {
        dataBarang_tabBut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dataBarang_tabBut.setBackground(new Color(255, 186, 0));
    }

    private void dataBarang_tabButMouseExited(java.awt.event.MouseEvent evt) {
        dataBarang_tabBut.setBackground(new Color(255, 153, 0));
    }

    // sidepane data supplier
    private void dataSupplier_tabButMouseClicked(java.awt.event.MouseEvent evt) {
        panelBarang.setVisible(false);
        panelTransasksi.setVisible(false);
        panelSupplier.setVisible(true);
        panelLaporanTr.setVisible(false);
    }

    private void dataSupplier_tabButMouseEntered(java.awt.event.MouseEvent evt) {
        dataSupplier_tabBut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dataSupplier_tabBut.setBackground(new Color(255, 186, 0));
    }

    private void dataSupplier_tabButMouseExited(java.awt.event.MouseEvent evt) {
        dataSupplier_tabBut.setBackground(new Color(255, 153, 0));
    }

    // sidepanel transaksi
    private void transaksi_tabButMouseClicked(java.awt.event.MouseEvent evt) {
        panelBarang.setVisible(false);
        panelTransasksi.setVisible(true);
        panelSupplier.setVisible(false);
        panelLaporanTr.setVisible(false);
    }

    private void transaksi_tabButMouseExited(java.awt.event.MouseEvent evt) {
        transaksi_tabBut.setBackground(new Color(255, 153, 0));
    }

    private void transaksi_tabButMouseEntered(java.awt.event.MouseEvent evt) {
        transaksi_tabBut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        transaksi_tabBut.setBackground(new Color(255, 186, 0));
    }

    // sidepanel data penjualan
    private void dataPenjualan_tabButMouseClicked(java.awt.event.MouseEvent evt) {
        panelBarang.setVisible(false);
        panelTransasksi.setVisible(false);
        panelSupplier.setVisible(false);
        panelLaporanTr.setVisible(true);
    }

    private void dataPenjualan_tabButMouseEntered(java.awt.event.MouseEvent evt) {
        dataPenjualan_tabBut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dataPenjualan_tabBut.setBackground(new Color(255, 186, 0));
    }

    private void dataPenjualan_tabButMouseExited(java.awt.event.MouseEvent evt) {
        dataPenjualan_tabBut.setBackground(new Color(255, 153, 0));
    }

    // event label log_out
    private void logout_labelMouseEntered(java.awt.event.MouseEvent evt) {
        logout_label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void logout_labelMouseClicked(java.awt.event.MouseEvent evt) {
        int ok = JOptionPane.showConfirmDialog(null, "Apakah anda ingi keluar", "Log Out", JOptionPane.YES_NO_OPTION);
        if (ok == 0) {
            this.setVisible(false);
            new loginForm().setVisible(true);
        }
    }

    // EVENT PANEL DATA BARANG
    private void BTinsert_barangActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            stat = con.createStatement();
            stat.executeUpdate("Insert Into databarang Values('" + JTkode_barang.getText() + "','"
                    + JTnama_barang.getText() + "','" + JTsatuan_barang.getText() + "','" + JTsupplier_barang.getText()
                    + "','" + JTstok_barang.getText() + "','" + JTharga_barang.getText() + "')");
            kosongkan_formBarang();
            loadTabel_barang();
            JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Data Gagal Disimpan " + e);
        }
    }

    private void tabel_barangMouseClicked(java.awt.event.MouseEvent evt) {
        int i_FadilArdiansyah = tabel_barang.getSelectedRow();
        if (i_FadilArdiansyah == -1) {
            return;
        }

        id_barang = (String) tabel_barang.getValueAt(i_FadilArdiansyah, 0);
        String data1 = (String) tabel_barang.getValueAt(i_FadilArdiansyah, 1);
        String data2 = (String) tabel_barang.getValueAt(i_FadilArdiansyah, 2);
        String data3 = (String) tabel_barang.getValueAt(i_FadilArdiansyah, 3);
        String data4 = (String) tabel_barang.getValueAt(i_FadilArdiansyah, 4);
        String data5 = (String) tabel_barang.getValueAt(i_FadilArdiansyah, 5);
        String data6 = (String) tabel_barang.getValueAt(i_FadilArdiansyah, 6);

        JTkode_barang.setText(id_barang);
        JTnama_barang.setText(data1);
        JTsatuan_barang.setText(data2);
        JTsupplier_barang.setText(data3);
        CBnamaSupplier.setSelectedItem(data4);
        JTstok_barang.setText(data5);
        JTharga_barang.setText(data6);
    }

    private void BTedit_barangActionPerformed(java.awt.event.ActionEvent evt) {
        int ok = JOptionPane.showConfirmDialog(null, "Apakah ingin mengupdate data ini?", "Edit Data",
                JOptionPane.YES_NO_CANCEL_OPTION);
        try {
            pst = con.prepareStatement(
                    "Update databarang set kode_barang=?, nama_barang=?, satuan=?, id_supplier=?, stok=?, harga=? Where kode_barang='"
                            + id_barang + "'");
            if (ok == 0) {
                try {
                    pst.setString(1, JTkode_barang.getText());
                    pst.setString(2, JTnama_barang.getText());
                    pst.setString(3, JTsatuan_barang.getText());
                    pst.setString(4, JTsupplier_barang.getText());
                    pst.setInt(5, Integer.parseInt(JTstok_barang.getText()));
                    pst.setInt(6, Integer.parseInt(JTharga_barang.getText()));
                    pst.executeUpdate();
                    loadTabel_barang();
                    kosongkan_formBarang();

                    JOptionPane.showMessageDialog(null, "Data Berhasil Di Update");
                } catch (HeadlessException | NumberFormatException | SQLException e) {
                    JOptionPane.showMessageDialog(null, "Data Gagal Di Update " + e);
                }
            }
        } catch (HeadlessException | SQLException e) {
        }
    }

    private void BTdelete_barangActionPerformed(java.awt.event.ActionEvent evt) {
        int ok = JOptionPane.showConfirmDialog(null, "Apakah Yakin Menghapus Data?", "Hapus Data",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (ok == 0) {
            try {
                String sql = "Delete From tbl_bayarkas where KodeBayar='" + JTkode_barang.getText() + "'";
                pst = con.prepareStatement(sql);
                pst.executeUpdate();
                loadTabel_barang();
                kosongkan_formBarang();

                JOptionPane.showMessageDialog(null, "Hapus Data Berhasil");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Hapus Data Gagal " + e);
            }
        }
    }

    private void JTsupplier_barangMouseClicked(java.awt.event.MouseEvent evt) {
        idSupplier_barang();
    }

    private void BTrefresh_barangActionPerformed(java.awt.event.ActionEvent evt) {
        comboBox_formbarang();
        kosongkan_formBarang();
        loadTabel_barang();
    }

    // EVENT PANEL DATA SUPPLIER
    private void BTadd_supplierActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            stat = con.createStatement();
            stat.executeUpdate("Insert Into datasupplier Values('" + JTidSupplier_supplier.getText() + "','"
                    + JTnama_supplier.getText() + "','" + JTnomor_supplier.getText() + "','"
                    + JTalamat_supplier.getText() + "')");
            kosongkan_formSupplier();
            loadTabel_supplier();
            JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Data Gagal Disimpan " + e);
        }
    }

    private void BTedit_supplierActionPerformed(java.awt.event.ActionEvent evt) {
        int ok = JOptionPane.showConfirmDialog(null, "Apakah ingin mengupdate data ini?", "Edit Data",
                JOptionPane.YES_NO_CANCEL_OPTION);
        try {
            pst = con.prepareStatement(
                    "Update datasupplier set id_supplier=?, nama_supplier=?, no_telepon=?, alamat=? Where id_supplier='"
                            + id_supplier + "'");
            if (ok == 0) {
                try {
                    pst.setString(1, JTidSupplier_supplier.getText());
                    pst.setString(2, JTnama_supplier.getText());
                    pst.setString(3, JTnomor_supplier.getText());
                    pst.setString(4, JTalamat_supplier.getText());
                    pst.executeUpdate();
                    loadTabel_supplier();
                    kosongkan_formSupplier();

                    JOptionPane.showMessageDialog(null, "Data Berhasil Di Update");
                } catch (HeadlessException | NumberFormatException | SQLException e) {
                    JOptionPane.showMessageDialog(null, "Data Gagal Di Update " + e);
                }
            }
        } catch (HeadlessException | SQLException e) {
        }
    }

    private void BTdelete_supplierActionPerformed(java.awt.event.ActionEvent evt) {
        int ok = JOptionPane.showConfirmDialog(null, "Apakah Yakin Menghapus Data?", "Hapus Data",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (ok == 0) {
            try {
                String sql = "Delete From datasupplier where id_supplier='" + JTidSupplier_supplier.getText() + "'";
                pst = con.prepareStatement(sql);
                pst.executeUpdate();
                loadTabel_supplier();
                kosongkan_formSupplier();

                JOptionPane.showMessageDialog(null, "Hapus Data Berhasil");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Hapus Data Gagal " + e);
            }
        }
    }

    private void BTrefresh_supplierActionPerformed(java.awt.event.ActionEvent evt) {
        kosongkan_formSupplier();
        loadTabel_supplier();
    }

    private void tabel_supplierMouseClicked(java.awt.event.MouseEvent evt) {
        int i_FadilArdiansyah = tabel_supplier.getSelectedRow();
        if (i_FadilArdiansyah == -1) {
            return;
        }

        id_supplier = (String) tabel_supplier.getValueAt(i_FadilArdiansyah, 0);
        String data1 = (String) tabel_supplier.getValueAt(i_FadilArdiansyah, 1);
        String data2 = (String) tabel_supplier.getValueAt(i_FadilArdiansyah, 2);
        String data3 = (String) tabel_supplier.getValueAt(i_FadilArdiansyah, 3);

        JTidSupplier_supplier.setText(id_supplier);
        JTnama_supplier.setText(data1);
        JTnomor_supplier.setText(data2);
        JTalamat_supplier.setText(data3);
    }

    // EVENT PANEL TRANSAKSI
    private void cariBarang_jButtonActionPerformed(java.awt.event.ActionEvent evt) {
        cari_barang();
    }

    private void tambahCart_jButtonActionPerformed(java.awt.event.ActionEvent evt) {
        subtotal();
        String kode_detail = "D" + kodeTransaki_jTextField.getText();
        String hargaBarang = String.valueOf(harga_barang);
        String diskonBarang = String.valueOf(diskon);
        String subTotal = String.valueOf(sub_total);
        String jumlahBarang = JumlahBarang_jTextField.getText();

        modelList.addRow(new Object[] { kode_detail, kode_barang, hargaBarang, jumlahBarang, diskonBarang, subTotal });
        sum();

        JumlahBarang_jTextField.setText("");
        discountBarang_jTextField.setText("");
    }

    private void hasilCariBarang_jTableMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            int row = hasilCariBarang_jTable.getSelectedRow();
            String kodeBarang = (String) hasilCariBarang_jTable.getValueAt(row, 0);
            String sql = "select * from databarang where kode_barang='" + kodeBarang + "'";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();
            if (res.next()) {
                kode_barang = res.getString("kode_barang");
                harga_barang = res.getInt("harga");
                stok_barang = res.getInt("stok");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void kurangiCart_jButtonActionPerformed(java.awt.event.ActionEvent evt) {
        DefaultTableModel model = (DefaultTableModel) listCartBarang_jTable.getModel();
        int[] rows = listCartBarang_jTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            model.removeRow(rows[i] - i);
        }
        sum();
    }

    private void kembalian_jTextFieldMouseClicked(java.awt.event.MouseEvent evt) {
        int totalBiaya = Integer.parseInt(totalBiaya_jTextField.getText());
        int bayar = Integer.parseInt(bayarTrans_jTextField.getText());
        int kembalian = bayar - totalBiaya;
        kembalian_jTextField.setText(String.valueOf(kembalian));
    }

    private void simpanTrans_jButtonActionPerformed(java.awt.event.ActionEvent evt) {
        simpan_transaksi();
        simpan_detailTransaksi();
    }

    // EVENT PANEL LAPORAN PENJUALAN
    private void dataTransaksi_jTableMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            int row = dataTransaksi_jTable.getSelectedRow();
            String Kode_detail = (String) dataTransaksi_jTable.getValueAt(row, 1);
            String sql = "select * from detail_trans_penjualan where kode_detail_transaksi='" + Kode_detail + "'";
            pst = con.prepareStatement(sql);
            res = pst.executeQuery();
            detailTransaksi_jTable.setModel(DbUtils.resultSetToTableModel(res));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // =========================================================================================================

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTadd_supplier;
    private javax.swing.JButton BTdelete_barang;
    private javax.swing.JButton BTdelete_supplier;
    private javax.swing.JButton BTedit_barang;
    private javax.swing.JButton BTedit_supplier;
    private javax.swing.JButton BTinsert_barang;
    private javax.swing.JButton BTrefresh_barang;
    private javax.swing.JButton BTrefresh_supplier;
    private javax.swing.JComboBox<String> CBnamaSupplier;
    private javax.swing.JTextField JTalamat_supplier;
    private javax.swing.JTextField JTharga_barang;
    private javax.swing.JTextField JTidSupplier_supplier;
    private javax.swing.JTextField JTkode_barang;
    private javax.swing.JTextField JTnama_barang;
    private javax.swing.JTextField JTnama_supplier;
    private javax.swing.JTextField JTnomor_supplier;
    private javax.swing.JTextField JTsatuan_barang;
    private javax.swing.JTextField JTstok_barang;
    private javax.swing.JTextField JTsupplier_barang;
    private javax.swing.JTextField Jam_jTextField;
    private javax.swing.JTextField JumlahBarang_jTextField;
    private javax.swing.JTextField Tanggal_jTextField1;
    private javax.swing.JPanel background;
    private javax.swing.JTextField bayarTrans_jTextField;
    private javax.swing.JButton cariBarang_jButton;
    private javax.swing.JTextField cariBarang_jTextField;
    private javax.swing.JPanel dataBarang_tabBut;
    private javax.swing.JLabel dataBarang_tg;
    private javax.swing.JPanel dataPenjualan_tabBut;
    private javax.swing.JPanel dataSupplier_tabBut;
    private javax.swing.JTable dataTransaksi_jTable;
    private javax.swing.JTable detailTransaksi_jTable;
    private javax.swing.JTextField discountBarang_jTextField;
    private javax.swing.JTable hasilCariBarang_jTable;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextField kembalian_jTextField;
    private javax.swing.JTextField kodeTransaki_jTextField;
    private javax.swing.JButton kurangiCart_jButton;
    private javax.swing.JTable listCartBarang_jTable;
    private javax.swing.JLabel logout_label;
    private javax.swing.JPanel mainPanel;
    public static final javax.swing.JLabel namaUser_jLabel1 = new javax.swing.JLabel();
    private javax.swing.JPanel panelBarang;
    private javax.swing.JPanel panelLaporanTr;
    private javax.swing.JPanel panelSupplier;
    private javax.swing.JPanel panelTransasksi;
    private javax.swing.JPanel sidePanel;
    private javax.swing.JButton simpanTrans_jButton;
    private javax.swing.JTable tabel_barang;
    private javax.swing.JTable tabel_supplier;
    private javax.swing.JButton tambahCart_jButton;
    private javax.swing.JTextField totalBiaya_jTextField;
    private javax.swing.JButton transBaru_jButton;
    private javax.swing.JPanel transaksi_tabBut;
    private javax.swing.JPanel user_panel;
    public static final javax.swing.JLabel username_jLabel = new javax.swing.JLabel();
    // End of variables declaration//GEN-END:variables
}