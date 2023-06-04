package com.example.pengaduan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pengaduan.R;
import com.example.pengaduan.model.Aspirasi;

import java.util.ArrayList;

public class AdapterAspirasi extends RecyclerView.Adapter<AdapterAspirasi.ListViewHolder> {
    private ArrayList<Aspirasi> list;

    private OnItemDeleteClickListener deleteClickListener;

    public AdapterAspirasi(ArrayList<Aspirasi> list) {
        this.list = list;
    }

    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(int position);
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView nama;
        TextView jenis;
        TextView deskripsi;
        ImageView imgPhoto;
        ImageView deleteIcon;

        public ListViewHolder(View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.nama);
            jenis = itemView.findViewById(R.id.aspirasi);
            deskripsi = itemView.findViewById(R.id.deskripsi);
            imgPhoto = itemView.findViewById(R.id.imgDummy);
            deleteIcon = itemView.findViewById(R.id.delete);

        }
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aspirasi, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        Aspirasi aspirasi = list.get(position);
        holder.nama.setText(aspirasi.getNamaPengirim());
        holder.jenis.setText(aspirasi.getJenisAspirasi());
        holder.deskripsi.setText(aspirasi.getDeskripsi());
        Glide.with(holder.itemView.getContext())
                .load("https://www.pngall.com/wp-content/uploads/5/Vector-Checklist-PNG-HD-Image.png")
                .into(holder.imgPhoto);
        holder.deleteIcon.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onItemDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
