package com.example.vklinkopener;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.ViewHolder> {

    private ArrayList<String> urls;
    private OnItemDeletedListener deleteListener;

    public interface OnItemDeletedListener {
        void onItemDeleted(int position);
    }

    public UrlAdapter(ArrayList<String> urls, OnItemDeletedListener listener) {
        this.urls = urls;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_url, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = urls.get(position);
        holder.tvUrl.setText(url);
        holder.tvIndex.setText(String.valueOf(position + 1));

        holder.btnOpen.setOnClickListener(v -> {
            try {
                String openUrl = url;
                if (!openUrl.startsWith("http://") && !openUrl.startsWith("https://")) {
                    openUrl = "https://" + openUrl;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(v.getContext(), "Loi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onItemDeleted(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvUrl;
        ImageButton btnOpen, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvUrl = itemView.findViewById(R.id.tvUrl);
            btnOpen = itemView.findViewById(R.id.btnOpen);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
