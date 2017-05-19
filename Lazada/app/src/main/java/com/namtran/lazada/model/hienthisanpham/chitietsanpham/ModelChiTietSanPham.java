package com.namtran.lazada.model.hienthisanpham.chitietsanpham;

import android.util.Log;

import com.namtran.lazada.connection.internet.DownloadJSON;
import com.namtran.lazada.model.objectclass.Action;
import com.namtran.lazada.model.objectclass.ChiTietSanPham;
import com.namtran.lazada.model.objectclass.DanhGia;
import com.namtran.lazada.model.objectclass.SanPham;
import com.namtran.lazada.view.trangchu.TrangChuActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by namtr on 5/18/2017.
 */

public class ModelChiTietSanPham {

    public SanPham layChiTietSanPham(Action action, int maSP) {
        SanPham sanPham = new SanPham();
        String url = TrangChuActivity.SERVER_NAME + action.getAction();

        if (!url.contains(".php"))
            url += ".php";

        List<HashMap<String, String>> attrs = new ArrayList<>();

        HashMap<String, String> attr = new HashMap<>();
        attr.put("MASP", String.valueOf(maSP));
        attrs.add(attr);

        DownloadJSON downloadJSON = new DownloadJSON(url, attrs);
        downloadJSON.execute();

        try {
            String json = downloadJSON.get();
            if (json == null) return null;

            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(action.getNodeName());

            JSONObject object = array.getJSONObject(0);
            sanPham.setMaSP(object.getInt("MASP"));
            sanPham.setTenSP(object.getString("TENSP"));
            sanPham.setGia(object.getInt("GIA"));
            sanPham.setAnhLon(object.getString("ANHLON"));
            sanPham.setAnhNho(object.getString("ANHNHO"));
            sanPham.setMaLoaiSP(object.getInt("MALOAISP"));
            sanPham.setThongTin(object.getString("THONGTIN"));
            sanPham.setMaThuongHieu(object.getInt("MATHUONGHIEU"));
            sanPham.setMaNV(object.getInt("MANV"));
            sanPham.setTenNV(object.getString("TENNV"));
            sanPham.setLuotMua(object.getInt("LUOTMUA"));

            List<ChiTietSanPham> chiTietSanPhams = new ArrayList<>();
            JSONArray thongsokythuat = object.getJSONArray("THONGSOKYTHUAT");
            int count = thongsokythuat.length();
            for (int i = 0; i < count; i++) {
                JSONObject chitiet = thongsokythuat.getJSONObject(i);
                for (int j = 0; j < chitiet.names().length(); j++) {
                    ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
                    String tenChiTiet = chitiet.names().getString(j);
                    chiTietSanPham.setTen(tenChiTiet);
                    chiTietSanPham.setGiaTri(chitiet.getString(tenChiTiet));
                    chiTietSanPhams.add(chiTietSanPham);
                }
            }
            sanPham.setChiTietSanPhams(chiTietSanPhams);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            return null;
        }
        return sanPham;
    }

    // limit <= 0 nghĩa là lấy tất các đánh giá
    public List<DanhGia> layDanhSachDanhGia(Action action, int maSP, int limit) {
        List<DanhGia> danhGiaList = new ArrayList<>();
        String url = TrangChuActivity.SERVER_NAME + action.getAction();
        if (!url.contains(".php")) url += ".php";

        List<HashMap<String, String>> attrs = new ArrayList<>();

        HashMap<String, String> attr1 = new HashMap<>();
        attr1.put("masp", String.valueOf(maSP));
        attrs.add(attr1);

        HashMap<String, String> attr2 = new HashMap<>();
        attr2.put("limit", String.valueOf(limit));
        attrs.add(attr2);

        DownloadJSON downloadJSON = new DownloadJSON(url, attrs);
        downloadJSON.execute();

        try {
            String json = downloadJSON.get();
            if (json == null) return null;

            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(action.getNodeName());

            int len = array.length();
            for (int i = 0; i < len; i++) {
                DanhGia danhGia = new DanhGia();
                JSONObject object = array.getJSONObject(i);

                danhGia.setMaSP(object.getInt("MASP"));
                String tieuDe = object.getString("TIEUDE");
                danhGia.setTieuDe(tieuDe.substring(0, 1).toUpperCase() + tieuDe.substring(1));
                danhGia.setNoiDung(object.getString("NOIDUNG"));
                danhGia.setTenThietBi(object.getString("TENTHIETBI"));
                danhGia.setSoSao(object.getInt("SOSAO"));
                danhGia.setMaDG("MADG");
                danhGia.setNgayDanhGia(object.getString("NGAYDANHGIA"));

                danhGiaList.add(danhGia);
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            return null;
        }
        return danhGiaList;
    }
}