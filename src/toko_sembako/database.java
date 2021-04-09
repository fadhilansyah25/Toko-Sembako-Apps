/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toko_sembako;
import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Fadhilansyah25
 */
public class database {
    Connection koneksi = null;
    Statement stat = null;

    public static Connection koneksiDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection koneksi = DriverManager.getConnection("jdbc:mysql://localhost:3306/toko_sembako", "root", "");
            return koneksi;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
}
