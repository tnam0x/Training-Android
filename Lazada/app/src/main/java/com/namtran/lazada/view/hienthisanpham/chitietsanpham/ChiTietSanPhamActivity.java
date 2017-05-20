package com.namtran.lazada.view.hienthisanpham.chitietsanpham;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.namtran.lazada.R;
import com.namtran.lazada.adapter.DanhGiaAdapter;
import com.namtran.lazada.adapter.ViewPagerCTSPAdapter;
import com.namtran.lazada.model.dangnhap_dangky.ModelDangNhap;
import com.namtran.lazada.model.objectclass.Action;
import com.namtran.lazada.model.objectclass.ChiTietSanPham;
import com.namtran.lazada.model.objectclass.DanhGia;
import com.namtran.lazada.model.objectclass.SanPham;
import com.namtran.lazada.presenter.hienthisanpham.chitietsanpham.PresenterChiTietSanPham;
import com.namtran.lazada.presenter.trangchu.xulymenu.PresenterXuLyMenu;
import com.namtran.lazada.tools.Converter;
import com.namtran.lazada.view.dangnhap_dangky.DangNhapVaDangKyActivity;
import com.namtran.lazada.view.hienthisanpham.chitietsanpham.fragment.FragmentChiTietSP;
import com.namtran.lazada.view.hienthisanpham.danhgia.DanhSachDanhGiaActivity;
import com.namtran.lazada.view.hienthisanpham.danhgia.ThemDanhGiaActivity;
import com.namtran.lazada.view.trangchu.TrangChuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namtr on 5/16/2017.
 */

public class ChiTietSanPhamActivity extends AppCompatActivity implements ViewChiTietSanPham, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private ViewPager mSlider;
    private List<Fragment> mFragment;
    private LinearLayout mDotLayout;
    private TextView mTVTenSP, mTVGia;
    private ImageButton mButtonFavorite;
    private boolean mIsFavorite; // lưu trạng thái mButtonFavorite
    private TextView mTVNguoiBan;
    private TextView mTVThongTinChiTiet;
    private ImageButton mButtonXemThemThongTin;
    private boolean mIsExpanded; // lưu trạng thái mButtonXemThemThongTin
    private LinearLayout mLayoutThongSoKyThuat;
    private SanPham mSanPhamGioHang;
    private int masp;
    private RecyclerView mRecyclerDanhGia;
    private RatingBar mRating;
    private PresenterChiTietSanPham mPresenterChiTietSanPham;
    private TextView mTVSoLuongSPTrongGioHang;
    private PresenterXuLyMenu mPresenterXuLyMenu;
    private AccessToken mFbToken;
    private GoogleSignInResult mGgToken;
    private Menu mMenu;
    private MenuItem mLoginItem;
    private ModelDangNhap mModelDangNhap;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitietsanpham);

        // initialize
        init();
        mFragment = new ArrayList<>();

        mPresenterXuLyMenu = new PresenterXuLyMenu();
        mModelDangNhap = new ModelDangNhap();
        mGoogleApiClient = mModelDangNhap.layGoogleApiClient(this, this);

        // retrieve data
        masp = getIntent().getIntExtra("MASP", -1);
        mPresenterChiTietSanPham = new PresenterChiTietSanPham(this);
        mPresenterChiTietSanPham.layChiTietSanPham(Action.CHI_TIET_SAN_PHAM, masp);
        mPresenterChiTietSanPham.layDanhSachDanhGia(Action.DANH_SACH_DANH_GIA, masp, 0);

        setupDotLayout(0);
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.chitietsanpham_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
        mSlider = (ViewPager) findViewById(R.id.chitietsanpham_viewPager_hinhanh);
        mSlider.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setupDotLayout(position);
            }
        });
        mDotLayout = (LinearLayout) findViewById(R.id.chitietsanpham_layout_dot);
        mTVTenSP = (TextView) findViewById(R.id.chitietsanpham_tv_tensanpham);
        mTVGia = (TextView) findViewById(R.id.chitietsanpham_tv_gia);
        mButtonFavorite = (ImageButton) findViewById(R.id.chitietsanpham_btn_yeuthich);
        mButtonFavorite.setOnClickListener(this);
        mTVNguoiBan = (TextView) findViewById(R.id.chitietsanpham_tv_nguoiban);
        mTVThongTinChiTiet = (TextView) findViewById(R.id.chitietsanpham_tv_chitietsp);
        mButtonXemThemThongTin = (ImageButton) findViewById(R.id.chitietsanpham_btn_xemthem_thongtin);
        mLayoutThongSoKyThuat = (LinearLayout) findViewById(R.id.chitietsanpham_layout_thongsokythuat);

        TextView tvVietDanhGia = (TextView) findViewById(R.id.chitietsanpham_tv_danhgia);
        tvVietDanhGia.setOnClickListener(this);
        mRecyclerDanhGia = (RecyclerView) findViewById(R.id.chitietsanpham_recycler_danhgia);
        Button btnXemTatCaDanhGia = (Button) findViewById(R.id.chitietsanpham_btn_xemtatca_danhgia);
        btnXemTatCaDanhGia.setOnClickListener(this);
        mRating = (RatingBar) findViewById(R.id.chitietsanpham_ratingbar_danhgia);

        ImageButton btnThemGioHang = (ImageButton) findViewById(R.id.chitietsanpham_btn_giohang);
        btnThemGioHang.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        mLoginItem = menu.findItem(R.id.menu_login);
        MenuItem item = menu.findItem(R.id.menu_cart);
        View actionLayout = item.getActionView();
        mTVSoLuongSPTrongGioHang = (TextView) actionLayout.findViewById(R.id.item_cart_tv_numberOfItems);

        capNhatGioHang();
        capNhatMenu(menu);
        // lưu menu hiện tại
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_login:
                String tenNV = mModelDangNhap.layCacheDangNhap(this);
                if (mFbToken == null && mGgToken == null && tenNV.equals("")) { // chưa đăng nhập
                    Intent loginIntent = new Intent(this, DangNhapVaDangKyActivity.class);
                    startActivityForResult(loginIntent, TrangChuActivity.REQUEST_CODE_LOGIN);
                }
                break;
            case R.id.menu_logout:
                // đăng xuất
                if (mFbToken != null) {
                    LoginManager.getInstance().logOut();
                } else if (mGgToken != null) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                } else {
                    // xóa tên người dùng lưu trong cache
                    mModelDangNhap.capNhatCacheDangNhap(this, "");
                }
                Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                capNhatMenu(mMenu);
                break;
        }
        return true;
    }

    private void capNhatMenu(Menu menu) {
        MenuItem logoutItem = menu.findItem(R.id.menu_logout);

        kiemTraDangNhapFB();
        kiemTraDangNhapGG();
        kiemTraDangNhapEmail();

        String tenNV = mModelDangNhap.layCacheDangNhap(this);
        // nếu đã đăng nhập thì hiển thị menu đăng xuất
        if (mFbToken != null || mGgToken != null || !tenNV.equals("")) {
            logoutItem.setVisible(true);
        } else {
            logoutItem.setVisible(false);
        }
    }

    // kiểm tra xem có đăng nhập bằng fb ko, nếu có thì hiển thị tên người dùng ra menu
    private void kiemTraDangNhapFB() {
        mFbToken = mPresenterXuLyMenu.layTokenNguoiDungFB();
        // lấy thông tin người dùng từ facebookToken
        if (mFbToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(mFbToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        if (object != null) {
                            String name = object.getString("name");
                            mLoginItem.setTitle(String.valueOf("Hi, " + name));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            // truyền các tham số cần lấy
            Bundle parameter = new Bundle();
            parameter.putString("fields", "name");
            request.setParameters(parameter);
            request.executeAsync();
        }
    }

    // kiểm tra xem có đăng nhập bằng gg ko, nếu có thì hiển thị tên người dùng ra menu
    private void kiemTraDangNhapGG() {
        mGgToken = mModelDangNhap.layKetQuaDangNhapGoogle(mGoogleApiClient);
        if (mGgToken != null && mGgToken.getSignInAccount() != null) {
            String name = mGgToken.getSignInAccount().getDisplayName();
            mLoginItem.setTitle(String.valueOf("Hi, " + name));
        }
    }

    // kiểm tra xem có đăng nhập bằng email ko, nếu có thì hiển thị tên người dùng ra menu
    private void kiemTraDangNhapEmail() {
        String tenNV = mModelDangNhap.layCacheDangNhap(this);
        if (!tenNV.equals("")) {
            mLoginItem.setTitle(String.valueOf("Hi, " + tenNV));
        }
    }

    private void capNhatGioHang() {
        long soLuong = mPresenterChiTietSanPham.soLuongSPCoTrongGioHang(this);
        if (soLuong == 0)
            mTVSoLuongSPTrongGioHang.setVisibility(View.GONE);
        else
            mTVSoLuongSPTrongGioHang.setVisibility(View.VISIBLE);
        mTVSoLuongSPTrongGioHang.setText(String.valueOf(soLuong));
    }

    @Override
    public void hienThiChiTietSanPham(SanPham sanPham) {
        if (sanPham != null) {
            mSanPhamGioHang = sanPham; // lưu sản phẩm hiện tại để xử lý khi thêm vào giỏ hàng
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(sanPham.getTenSP());
            }

            // hiển thị thông tin sản phẩm
            mTVTenSP.setText(sanPham.getTenSP());
            mTVGia.setText(Converter.formatCurrency(sanPham.getGia()));
            mTVNguoiBan.setText(sanPham.getTenNV());

            // hiển thị chi tiết sản phẩm, mặc định cho hiển thị 100 ký tự
            String chitietsanpham = sanPham.getThongTin();
            mTVThongTinChiTiet.setText(chitietsanpham.substring(0, 100));

            // setup button xem thêm
            if (chitietsanpham.length() <= 100) mButtonXemThemThongTin.setVisibility(View.GONE);
            else {
                mButtonXemThemThongTin.setVisibility(View.VISIBLE);
                mButtonXemThemThongTin.setTag(chitietsanpham);
                mButtonXemThemThongTin.setOnClickListener(this);
            }

            setupChiTietSPLayout(sanPham.getChiTietSanPhams());
        }
    }

    @Override
    public void hienThiSlider(String... links) {
        if (links != null && links.length > 0) {
            List<Fragment> fragments = new ArrayList<>();
            for (String aLink : links) {
                FragmentChiTietSP chiTietSP = new FragmentChiTietSP();
                Bundle bundle = new Bundle();
                bundle.putString("LINKANH", TrangChuActivity.SERVER_NAME + aLink);
                chiTietSP.setArguments(bundle);
                fragments.add(chiTietSP);
            }
            ViewPagerCTSPAdapter adapter = new ViewPagerCTSPAdapter(getSupportFragmentManager(), fragments);
            mSlider.setAdapter(adapter);

            mFragment = fragments;
        }
    }

    @Override
    public void hienThiDanhGia(List<DanhGia> danhGias) {
        if (danhGias != null && danhGias.size() > 0) {
            DanhGiaAdapter adapter = new DanhGiaAdapter(this, danhGias, 3);
            mRecyclerDanhGia.setNestedScrollingEnabled(false);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerDanhGia.setLayoutManager(layoutManager);
            mRecyclerDanhGia.setAdapter(adapter);

            // tính toán số sao cho ratingbar
            float rating = 0.0f;
            for (int i = 0; i < danhGias.size(); i++) {
                rating += danhGias.get(i).getSoSao();
            }
            rating /= danhGias.size();
            mRating.setRating(rating);
        }
    }

    @Override
    public void ketQuaThemGiohang(boolean result) {
        if (result) {
            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            // cập nhật lại số sản phẩm trong giỏ hàng của trên menu
            long soLuong = mPresenterChiTietSanPham.soLuongSPCoTrongGioHang(this);
            if (soLuong == 0)
                mTVSoLuongSPTrongGioHang.setVisibility(View.GONE);
            else
                mTVSoLuongSPTrongGioHang.setVisibility(View.VISIBLE);
            mTVSoLuongSPTrongGioHang.setText(String.valueOf(soLuong));
        } else Toast.makeText(this, "Sản phẩm đã có trong giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    // thêm dấu chấm biểu thị số trang cho viewpager
    private void setupDotLayout(int position) {
        mDotLayout.removeAllViews();

        for (int i = 0; i < mFragment.size(); i++) {
            TextView dot = new TextView(this);
            dot.setTextSize(30);
            dot.setText(Converter.fromHtml("&#8226;"));
            if (i == position)
                dot.setTextColor(ContextCompat.getColor(this, R.color.bgToolbar));
            else dot.setTextColor(ContextCompat.getColor(this, R.color.dotInactive));

            mDotLayout.addView(dot);
        }

    }

    private void setupChiTietSPLayout(List<ChiTietSanPham> chiTietSanPhams) {
        int len = chiTietSanPhams.size();
        for (int i = 0; i < len; i++) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhams.get(i);
            String row = chiTietSanPham.getTen() + ": " + chiTietSanPham.getGiaTri();
            TextView thongso = new TextView(this);
            thongso.setText(row);
            mLayoutThongSoKyThuat.addView(thongso);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.chitietsanpham_btn_yeuthich:
                mIsFavorite = !mIsFavorite;
                setImageResourceButton(id);
                break;
            case R.id.chitietsanpham_btn_xemthem_thongtin:
                mIsExpanded = !mIsExpanded;
                setImageResourceButton(id);
                break;
            case R.id.chitietsanpham_tv_danhgia:
                Intent danhGia = new Intent(this, ThemDanhGiaActivity.class);
                danhGia.putExtra("MASP", masp);
                startActivity(danhGia);
                break;
            case R.id.chitietsanpham_btn_xemtatca_danhgia:
                Intent danhSachDanhGia = new Intent(this, DanhSachDanhGiaActivity.class);
                danhSachDanhGia.putExtra("MASP", masp);
                startActivity(danhSachDanhGia);
                break;
            case R.id.chitietsanpham_btn_giohang:
                Fragment fragment = mFragment.get(mSlider.getCurrentItem());
                View view = fragment.getView();
                BitmapDrawable drawable;
                if (view != null) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.fragment_chitietsanpham_hinhanh);
                    drawable = (BitmapDrawable) imageView.getDrawable();
                } else {
                    // lấy hình mặc định
                    drawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.ic_image_black_24dp);
                }
                byte[] hinhAnh = Converter.drawableToByteArray(drawable);
                mSanPhamGioHang.setHinhAnh(hinhAnh);
                mPresenterChiTietSanPham.themGioHang(this, mSanPhamGioHang);
                break;
        }
    }

    private void setImageResourceButton(int id) {
        switch (id) {
            case R.id.chitietsanpham_btn_yeuthich:
                if (mIsFavorite)
                    mButtonFavorite.setImageResource(R.drawable.ic_favorite_orange_24dp);
                else
                    mButtonFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                break;
            case R.id.chitietsanpham_btn_xemthem_thongtin:
                if (mIsExpanded) {
                    mButtonXemThemThongTin.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    mTVThongTinChiTiet.setText((String) mButtonXemThemThongTin.getTag());
                    mLayoutThongSoKyThuat.setVisibility(View.VISIBLE);
                } else {
                    mButtonXemThemThongTin.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    mTVThongTinChiTiet.setText(((String) mButtonXemThemThongTin.getTag()).substring(0, 100));
                    mLayoutThongSoKyThuat.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }
}
