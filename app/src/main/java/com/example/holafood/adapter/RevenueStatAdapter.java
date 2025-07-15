package com.example.holafood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.R;
import com.example.holafood.model.RevenueStat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RevenueStatAdapter extends RecyclerView.Adapter<RevenueStatAdapter.RevenueStatViewHolder> {

    private List<RevenueStat> revenueStats;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public RevenueStatAdapter(List<RevenueStat> revenueStats) {
        this.revenueStats = revenueStats;
    }

    public void setRevenueStats(List<RevenueStat> revenueStats) {
        this.revenueStats = revenueStats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RevenueStatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revenue_stat, parent, false);
        return new RevenueStatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueStatViewHolder holder, int position) {
        RevenueStat stat = revenueStats.get(position);
        holder.periodText.setText(String.format("Thời gian: %s - %s",
                dateFormat.format(stat.getPeriodStart()),
                dateFormat.format(stat.getPeriodEnd())));
        holder.revenueText.setText(String.format("Doanh thu: %.2fđ", stat.getTotalRevenue()));
    }

    @Override
    public int getItemCount() {
        return revenueStats != null ? revenueStats.size() : 0;
    }

    static class RevenueStatViewHolder extends RecyclerView.ViewHolder {
        TextView periodText, revenueText;

        public RevenueStatViewHolder(@NonNull View itemView) {
            super(itemView);
            periodText = itemView.findViewById(R.id.period_text);
            revenueText = itemView.findViewById(R.id.revenue_text);
        }
    }
}