package entity;

import java.sql.*;

import DAO.HoaDonPhongDAO;

public class HoaDonPhong {
    private int maHD;
    private int tinhTrang;
    private Date ngayGioNhan;
    private Date ngayGioTra;

    private Phong phong;
    private KhachHang khachHang;

    public HoaDonPhong(int maHD, int tinhTrang, Date ngayGioNhan, Date ngayGioTra, Phong phong, KhachHang khachHang) {
        this.maHD = maHD;
        this.tinhTrang = tinhTrang;
        this.ngayGioNhan = ngayGioNhan;
        this.ngayGioTra = ngayGioTra;
        this.phong = phong;
        this.khachHang = khachHang;
    }

    public int getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(int tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public HoaDonPhong(ResultSet rs) throws SQLException {
        LoaiPhong loaiPhong = new LoaiPhong(rs.getInt("MaLoaiPhong"), rs.getString("TenLoaiPhong"),
                rs.getDouble("DonGia"));
        Phong tempPhong = new Phong(rs.getString("MaPhong"), rs.getInt("SucChua"), rs.getInt("SoGiuong"),
                rs.getString("ViTri"), rs.getInt("TinhTrangP"), loaiPhong);
        KhachHang kh = new KhachHang(rs.getInt("MaKH"), rs.getString("TenKH"), rs.getString("CMND"),
                rs.getDate("NgayHetHan"), rs.getString("LoaiKH"), rs.getInt("SoLanDatPhong"));

        this.maHD = rs.getInt("MaHD");
        this.tinhTrang = rs.getInt("TinhTrangHD");
        this.ngayGioNhan = rs.getDate("NgayGioNhan");
        this.ngayGioTra = rs.getDate("NgayGioTra");
        this.phong = tempPhong;
        this.khachHang = kh;
    }

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public Date getNgayGioNhan() {
        return ngayGioNhan;
    }

    public void setNgayGioNhan(Date ngayGioNhan) {
        this.ngayGioNhan = ngayGioNhan;
    }

    public Date getNgayGioTra() {
        return ngayGioTra;
    }

    public void setNgayGioTra(Date ngayGioTra) {
        this.ngayGioTra = ngayGioTra;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public double tinhTongTien(){
        double tongTien = phong.getLoaiPhong().getDonGia()*tinhSoNgay();
        System.out.println(tongTien);
        tongTien = tongTien - khachHang.getSoLanDatPhong()*3*(tongTien/100);
        return tongTien;
    }

    public int tinhSoNgay(){
        long d = ngayGioTra.getTime() - ngayGioNhan.getTime();
        System.out.println(ngayGioTra.compareTo(ngayGioNhan));
        
        return (int)d/86400000 + 1;
    }
    
    public boolean updateTinhTrang(int tinhTrang){
        long ml=System.currentTimeMillis(); 
        ml = ml/86400000*86400000;
        Date now = new Date(ml);

        if(tinhTrang == 1){
            if(compareDate(now, ngayGioNhan) == -1)
                return false;
        }
        this.setTinhTrang(tinhTrang);
        HoaDonPhongDAO hoaDonPhong_dao = new HoaDonPhongDAO();
        return hoaDonPhong_dao.updateTinhTrang(this.maHD, tinhTrang);
    }

    public int compareDate(Date d1, Date d2) {
        if (d1.toString().equals(d2.toString()))
            return 0;

        if (d1.before(d2))
            return -1;

        return 1;
    }
}
