package com.example.pengaduan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pengaduan.R;
import com.example.pengaduan.model.User;

import java.util.ArrayList;

public class AdapterPelanggan extends RecyclerView.Adapter<AdapterPelanggan.ListViewHolder> {
    private ArrayList<User> list;

    private static OnItemLongClickListener mListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mListener = listener;
    }

    public AdapterPelanggan(ArrayList<User> list) {
        this.list = list;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView nama;
        TextView jenis;
        TextView deskripsi;

        ImageView imgPhoto;

        public ListViewHolder(View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.nama);
            jenis = itemView.findViewById(R.id.aspirasi);
            deskripsi = itemView.findViewById(R.id.deskripsi);
            imgPhoto = itemView.findViewById(R.id.imgDummy);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemLongClick(position);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelanggan, parent, false);
        ListViewHolder viewHolder = new ListViewHolder(view);
        viewHolder.onLongClick(view);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        User user = list.get(position);
        holder.nama.setText(user.getNamaLengkap());
        holder.jenis.setText(user.getId());
        holder.deskripsi.setText(user.getUsername());
        Glide.with(holder.itemView.getContext())
                .load("https://www.pngall.com/wp-content/uploads/5/Vector-Checklist-PNG-HD-Image.png")
                .into(holder.imgPhoto);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
