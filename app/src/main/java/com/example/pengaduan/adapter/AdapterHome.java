package com.example.pengaduan.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pengaduan.R;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.view.AspirasiActivity;
import com.example.pengaduan.view.PengaduanActivity;
import com.example.pengaduan.view.RiwayatActivity;
import com.example.pengaduan.view.SettingActivity;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.MyAdapter> {
    private final Context context;
    private final SharedPrefManager sharedPrefManager;

    public AdapterHome(Context context) {
        this.context = context;
        sharedPrefManager = new SharedPrefManager(context);
    }

    @Override
    public MyAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyAdapter(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter holder, int position) {
        if (position == 0) {
            holder.image.setImageResource(R.drawable.imagebase);
            holder.image1.setImageResource(R.drawable.round_data_object_24);
            holder.back.setBackgroundColor(Color.parseColor("#E6E53935"));
            holder.text.setText("Pengaduan");
            holder.back.setOnClickListener(v -> {
                if (!sharedPrefManager.sPSudahLogin()) {
                    context.startActivity(
                            new Intent(context, PengaduanActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                    );
                } else {
                    Intent intent = new Intent(context, PengaduanActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
        if (position == 1) {
            holder.image.setImageResource(R.drawable.imagebase);
            holder.image1.setImageResource(R.drawable.round_data_object_24);
            holder.back.setBackgroundColor(Color.parseColor("#F236883A"));
            holder.text.setText("Riwayat");
            holder.back.setOnClickListener(v -> {
                if (!sharedPrefManager.sPSudahLogin()) {
                    context.startActivity(
                            new Intent(context, RiwayatActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                    );
                } else {
                    Intent intent = new Intent(context, RiwayatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
        if (position == 2) {
            holder.image.setImageResource(R.drawable.imagebase);
            holder.image1.setImageResource(R.drawable.round_data_object_24);
            holder.back.setBackgroundColor(Color.parseColor("#F2AF4576"));
            holder.text.setText("Aspirasi");
            holder.back.setOnClickListener(v -> {
                if (!sharedPrefManager.sPSudahLogin()) {
                    context.startActivity(
                            new Intent(context, AspirasiActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                    );
                } else {
                    Intent intent = new Intent(context, AspirasiActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
        if (position == 3) {
            holder.image.setImageResource(R.drawable.imagebase);
            holder.image1.setImageResource(R.drawable.round_data_object_24);
            holder.back.setBackgroundColor(Color.parseColor("#F2EEAA45"));
            holder.text.setText("Setting");
            holder.back.setOnClickListener(v -> {
                if (!sharedPrefManager.sPSudahLogin()) {
                    context.startActivity(
                            new Intent(context, SettingActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                    );
                } else {
                    Intent intent = new Intent(context, SettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public static class MyAdapter extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView image1;
        TextView text;
        RelativeLayout back;

        public MyAdapter(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            image1 = itemView.findViewById(R.id.image1);
            text = itemView.findViewById(R.id.text);
            back = itemView.findViewById(R.id.back);
        }
    }
}
