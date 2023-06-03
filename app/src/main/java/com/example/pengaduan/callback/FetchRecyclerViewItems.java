package com.example.pengaduan.callback;

import android.view.View;

import com.example.pengaduan.model.Aduan;

public interface FetchRecyclerViewItems {
    void onItemClicked(View view, Aduan aduan);
    void onIntent(Aduan aduan);
}