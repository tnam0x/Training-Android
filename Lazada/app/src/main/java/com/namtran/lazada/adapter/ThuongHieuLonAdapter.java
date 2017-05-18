package com.namtran.lazada.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.namtran.lazada.R;
import com.namtran.lazada.customview.ButtonRippleDrawable;
import com.namtran.lazada.model.objectclass.ThuongHieu;
import com.namtran.lazada.tools.RippleMixer;
import com.namtran.lazada.view.hienthisanpham.theodanhmuc.HienThiSPTheoDanhMuc;
import com.namtran.lazada.view.hienthisanpham.theodanhmuc.ViewHienThiSPTheoDanhMuc;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by namtr on 08/05/2017.
 */

class ThuongHieuLonAdapter extends RecyclerView.Adapter<ThuongHieuLonAdapter.ViewHolder> {
    private Context mContext;
    private boolean mCheck;
    private List<ThuongHieu> thuongHieuList;
    private ButtonRippleDrawable rippleDrawable;

    ThuongHieuLonAdapter(Context context, List<ThuongHieu> thuongHieuList, boolean check) {
        this.mContext = context;
        this.mCheck = check;
        this.thuongHieuList = thuongHieuList;
        rippleDrawable = new ButtonRippleDrawable(Color.parseColor("#ffffff"), 0.2);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.custom_recycler_dientu_thuonghieulon, parent, false);
        // hiệu ứng khi nhấn vào một item
        v.setBackground(rippleDrawable.getRipple());

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ThuongHieu thuongHieu = thuongHieuList.get(position);
        holder.mTVTieuDe.setText(thuongHieu.getTenThuongHieu());
        Picasso.with(mContext).load(thuongHieu.getHinhThuongHieu()).placeholder(R.drawable.ic_image_black_24dp).resize(120, 120).into(holder.mIVHinhAnh);

        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailItemActivity = new Intent(mContext, HienThiSPTheoDanhMuc.class);
                detailItemActivity.putExtra("MALOAI", thuongHieu.getMaThuongHieu());
                detailItemActivity.putExtra("TENLOAI", thuongHieu.getTenThuongHieu());
                detailItemActivity.putExtra("CHECK", mCheck);
                mContext.startActivity(detailItemActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return thuongHieuList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTVTieuDe;
        ImageView mIVHinhAnh;
        CardView mItem;

        ViewHolder(View itemView) {
            super(itemView);
            mTVTieuDe = (TextView) itemView.findViewById(R.id.dientu_thuonghieulon_tenth);
            mIVHinhAnh = (ImageView) itemView.findViewById(R.id.dientu_thuonghieulon_hinhanh);
            mItem = (CardView) itemView.findViewById(R.id.dientu_thuonghieulon_item);
        }
    }
}
