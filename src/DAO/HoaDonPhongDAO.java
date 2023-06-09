package DAO;

import java.sql.*;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.*;

public class HoaDonPhongDAO {
    private static HoaDonPhongDAO instance = new HoaDonPhongDAO();

    public static HoaDonPhongDAO getInstance() {
        return instance;
    }

    public ArrayList<HoaDonPhong> getListHDPhong() {
        ArrayList<HoaDonPhong> dataList = new ArrayList<HoaDonPhong>();
        ConnectDB.getInstance();
        PreparedStatement stmt = null;
        Connection con = ConnectDB.getConnection();
        try {
            String sql = "select * from HoaDonPhong order by ngayGioNhan DESC";
            stmt = con.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int maHD = rs.getInt("MaHD");
                int tinhTrang = rs.getInt("TinhTrang");
                Date ngayGioNhan = rs.getDate("NgayGioNhan");
                Date ngayGioTra = rs.getDate("NgayGioTra");
                Phong phong = new Phong(rs.getString("MaPhong"));
                KhachHang khachHang = new KhachHang(rs.getInt("MaKH"));

                HoaDonPhong hdp = new HoaDonPhong(maHD, tinhTrang, ngayGioNhan, ngayGioTra, phong, khachHang);
                dataList.add(hdp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public ArrayList<HoaDonPhong> getListHDPhongByDate(Date tuNgay, Date denNgay) {
        ArrayList<HoaDonPhong> dataList = new ArrayList<HoaDonPhong>();
        ConnectDB.getInstance();
        PreparedStatement stmt = null;
        Connection con = ConnectDB.getConnection();
        try {
            String sql = "EXEC UDP_SearchHDPhongByDate ? , ? ";
            stmt = con.prepareStatement(sql);
            stmt.setDate(1, tuNgay);
            stmt.setDate(2, denNgay);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HoaDonPhong hdP = new HoaDonPhong(rs);
                dataList.add(hdP);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public ArrayList<HoaDonPhong> getListHDPhongByMaKH(int maKH, Date tuNgay, Date denNgay) {
        ArrayList<HoaDonPhong> dataList = new ArrayList<HoaDonPhong>();
        ConnectDB.getInstance();
        PreparedStatement stmt = null;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "EXEC UDP_SearchHDPhongByMaKH ? , ? , ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, maKH);
            stmt.setDate(2, tuNgay);
            stmt.setDate(3, denNgay);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HoaDonPhong hdP = new HoaDonPhong(rs);
                dataList.add(hdP);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public ArrayList<HoaDonPhong> getListHDPhongByTenKH(String tenKH, Date tuNgay, Date denNgay) {
        ArrayList<HoaDonPhong> dataList = new ArrayList<HoaDonPhong>();
        ConnectDB.getInstance();
        PreparedStatement stmt = null;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "EXEC UDP_SearchHDPhongByTenKH ? , ? , ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tenKH);
            stmt.setDate(2, tuNgay);
            stmt.setDate(3, denNgay);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HoaDonPhong hdP = new HoaDonPhong(rs);
                dataList.add(hdP);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public ArrayList<HoaDonPhong> getListHDPhongByMaKHAndTenKH(int maKH, String tenKH, Date tuNgay, Date denNgay) {
        ArrayList<HoaDonPhong> dataList = new ArrayList<HoaDonPhong>();
        ConnectDB.getInstance();
        PreparedStatement stmt = null;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "EXEC UDP_SearchHDPhongByMaKHAndTenKH ? , ? , ?, ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, maKH);
            stmt.setString(2, tenKH);
            stmt.setDate(3, tuNgay);
            stmt.setDate(4, denNgay);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HoaDonPhong hdP = new HoaDonPhong(rs);
                dataList.add(hdP);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public boolean insert(HoaDonPhong hdp) {
        int n = 0;
        try {
            ConnectDB.getInstance();
            Connection conn = ConnectDB.getConnection();

            // kiểm tra xem có thể đặt không
            String sql = "select * from HoaDonPhong where MaPhong = ? and tinhTrang != 2";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hdp.getPhong().getMaPhong());
            ResultSet rs = stmt.executeQuery();
            boolean flag = true;
            while (rs.next()) {
                if (compareDate(hdp.getNgayGioTra(), rs.getDate("NgayGioNhan")) == -1) {
                    continue;
                }
                if (compareDate(hdp.getNgayGioNhan(), rs.getDate("NgayGioTra")) == 1) {
                    continue;
                }
                flag = false;
                
            }

            if (!flag) {
                return false;
            }
            sql = "insert into HoaDonPhong values(?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, hdp.getKhachHang().getMaKH());
            statement.setString(2, hdp.getPhong().getMaPhong());
            statement.setInt(3, hdp.getTinhTrang());
            statement.setDate(4, hdp.getNgayGioNhan());
            statement.setDate(5, hdp.getNgayGioTra());
            
            n = statement.executeUpdate();

            // insert thành công
            if (n > 0) {
                // update tình trạng phòng
                Phong phong = hdp.getPhong();
                phong.updateTinhTrang(1);// đã đặt
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    public boolean delete(int maHD) {
        int n = 0;
        PreparedStatement stmt = null;
        ConnectDB.getInstance();
        Connection con = ConnectDB.getConnection();
        String query = "delete from dbo.HoaDonPhong where MaHD = ?";
        try {
            stmt = con.prepareStatement(query);
            stmt.setInt(1, maHD);

            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return n > 0;
    }

    public boolean updateTinhTrang(int maHD, int tinhTrang) {
        int n = 0;
        PreparedStatement stmt = null;
        ConnectDB.getInstance();
        Connection con = ConnectDB.getConnection();
        String query = "update dbo.HoaDonPhong set tinhTrang = ? Where MaHD = ?";
        try {
            stmt = con.prepareStatement(query);
            stmt.setInt(1, tinhTrang);
            stmt.setInt(2, maHD);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return n > 0;
    }

    public int compareDate(Date d1, Date d2) {
        if (d1.toString().equals(d2.toString()))
            return 0;

        if (d1.before(d2))
            return -1;

        return 1;
    }

    

    public HoaDonPhong getHDPByMaPhongAndDate(String maPhong){
        
        long ml=System.currentTimeMillis(); 
        System.out.print("truoc ");
        System.out.println(ml);
        ml = ml/86400000*86400000;
        System.out.print("sau ");
        System.out.println(ml);
        Date now = new Date(ml);
        HoaDonPhong hdp = null;
        try{
            ConnectDB.getInstance();
            Connection conn = ConnectDB.getConnection();

            String sql = "Select * from HoaDonPhong where MaPhong = ? and NgayGioNhan <= ? and NgayGioTra >= ? and TinhTrang != 2";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, maPhong);
            statement.setDate(2, now);
            statement.setDate(3, now);
            ResultSet rs = statement.executeQuery();
            System.out.println(maPhong);
            System.out.println(now);
            if(!rs.next())
                return null;
            
            int maHD = rs.getInt("MaHD");
            int tinhTrang = rs.getInt("TinhTrang");
            Date ngayGioNhan = rs.getDate("NgayGioNhan");
            Date ngayGioTra = rs.getDate("NgayGioTra");
            Phong phong = new Phong(rs.getString("MaPhong"));
            KhachHang khachHang = new KhachHang(rs.getInt("MaKH"));

            // HoaDonPhong ctdv = new HoaDonPhong(rs);
            hdp = new HoaDonPhong(maHD, tinhTrang, ngayGioNhan, ngayGioTra, phong, khachHang);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return hdp;
        
    }

    public ArrayList<HoaDonPhong> getListHDPhongByTinhTrang(int tinhTrang) {
        ArrayList<HoaDonPhong> dataList = new ArrayList<HoaDonPhong>();
        ConnectDB.getInstance();
        PreparedStatement stmt = null;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "select * from HoaDonPhong where tinhtrang = ? order by ngayGioNhan DESC";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, tinhTrang);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int maHD = rs.getInt("MaHD");
                // int tinhTrang = rs.getInt("TinhTrang");
                Date ngayGioNhan = rs.getDate("NgayGioNhan");
                Date ngayGioTra = rs.getDate("NgayGioTra");
                Phong phong = new Phong(rs.getString("MaPhong"));
                KhachHang khachHang = new KhachHang(rs.getInt("MaKH"));

                HoaDonPhong hdp = new HoaDonPhong(maHD, tinhTrang, ngayGioNhan, ngayGioTra, phong, khachHang);
                dataList.add(hdp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public int getLatestID() {
        int id = 0;
        ConnectDB.getInstance();
        Statement stmt = null;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM dbo.HoaDonPhong";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery(sql);
            rs.last();
            id = rs.getInt("MaHD");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public ArrayList<HoaDonPhong> getListHDPhongReservationLimit(String maPhong, Date tuNgay, Date denNgay) {
        ArrayList<HoaDonPhong> dataList = new ArrayList<HoaDonPhong>();
        ConnectDB.getInstance();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection con = ConnectDB.getConnection();
        String sql = "EXEC UDP_GetListHDPhongReservationLimit ?, ?, ? ";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhong);
            stmt.setDate(2, tuNgay);
            stmt.setDate(3, denNgay);

            rs = stmt.executeQuery();
            while (rs.next()) {
                HoaDonPhong phong = new HoaDonPhong(rs);
                dataList.add(phong);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public ArrayList<HoaDonPhong> getListHDPhongReservation(String maPhong, Date tuNgay) {
        ArrayList<HoaDonPhong> dataList = new ArrayList<HoaDonPhong>();
        ConnectDB.getInstance();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection con = ConnectDB.getConnection();
        String sql = "EXEC UDP_GetListHDPhongReservation ?, ? ";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhong);
            stmt.setDate(2, tuNgay);

            rs = stmt.executeQuery();
            while (rs.next()) {
                HoaDonPhong phong = new HoaDonPhong(rs);
                dataList.add(phong);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }
}
