package com.example.pengaduan.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.pengaduan.R;
import com.example.pengaduan.callback.FetchRecyclerViewItems;
import com.example.pengaduan.model.Aduan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TanggapanAdapter extends RecyclerView.Adapter<TanggapanAdapter.ProductViewHolder> {
    private final ArrayList<Aduan> aduans;
    private final FetchRecyclerViewItems listener;

    public TanggapanAdapter(ArrayList<Aduan> aduans, FetchRecyclerViewItems listener) {
        this.aduans = aduans;
        this.listener = listener;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView titikLokasi;
        TextView tanggal;
        TextView kondisiDevice;
        ConstraintLayout mainL;
        ImageView image;
        CardView button;

        public ProductViewHolder(View itemView) {
            super(itemView);
            titikLokasi = itemView.findViewById(R.id.titikLokasi);
            tanggal = itemView.findViewById(R.id.tanggal);
            kondisiDevice = itemView.findViewById(R.id.kondisiDevice);
            mainL = itemView.findViewById(R.id.mainLayout);
            image = itemView.findViewById(R.id.imgaduan);
            button = itemView.findViewById(R.id.detail);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Aduan currentTime = aduans.get(position);
        holder.titikLokasi.setText(currentTime.getTitikLokasi());
        holder.tanggal.setText(currentTime.getJenisAduan());
        holder.kondisiDevice.setText(currentTime.getTanggal());

        if (currentTime.getRawImage() != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(currentTime.getRawImage());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream);
            byte[] compressedImage = outputStream.toByteArray();
            Glide.with(holder.image.getContext())
                    .load(compressedImage)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            holder.image.setImageDrawable(resource);
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        } else {
            holder.image.setImageResource(R.drawable.kliklapor);
        }
        holder.mainL.setOnClickListener(view -> listener.onIntent(currentTime));
        updateDetailButtonText(holder.button, currentTime.getStatus());
        System.out.println(currentTime.getStatus()+ "INI STATUS");
        holder.button.setOnClickListener(view -> listener.onItemClicked(view, currentTime));
    }

    private void updateDetailButtonText(CardView cardView, String status) {
        TextView detailText = cardView.findViewById(R.id.detailText);
        switch (status) {
            case "Di Proses":
                detailText.setText("Di Proses");
                break;
            case "Selesai":
                detailText.setText("Selesai");
                break;
            case "Di Tolak":
                detailText.setText("Di Tolak");
                break;
            default:
                detailText.setText("Menunggu");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return aduans.size();
    }
}
