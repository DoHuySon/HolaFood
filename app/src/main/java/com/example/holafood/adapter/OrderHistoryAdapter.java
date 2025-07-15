package com.example.holafood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.R;
import com.example.holafood.model.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public OrderHistoryAdapter(List<Order> orders) {
        this.orders = orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderIdText.setText("Đơn hàng #" + order.getOrderId());
        holder.orderDateText.setText("Ngày: " + dateFormat.format(order.getCreatedAt()));
        holder.orderTotalText.setText("Tổng: " + order.getTotalAmount() + "đ");
        holder.orderStatusText.setText("Trạng thái: " + order.getStatus().getVietnameseName());
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, orderDateText, orderTotalText, orderStatusText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.order_id_text);
            orderDateText = itemView.findViewById(R.id.order_date_text);
            orderTotalText = itemView.findViewById(R.id.order_total_text);
            orderStatusText = itemView.findViewById(R.id.order_status_text);
        }
    }
}