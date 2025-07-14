package com.example.holafood.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.R;
import com.example.holafood.dao.OrderDao;
import com.example.holafood.dao.UserDao;
import com.example.holafood.model.Order;
import com.example.holafood.model.OrderStatus;
import com.example.holafood.model.PaymentMethod;
import com.example.holafood.model.User;
import com.example.holafood.OrderDetailSellerActivity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderSellerAdapter extends RecyclerView.Adapter<OrderSellerAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private OrderDao orderDao;
    private UserDao userDao;

    private static final Map<String, OrderStatus> STATUS_MAP = new HashMap<>();
    private static final Map<String, PaymentMethod> PAYMENT_MAP = new HashMap<>();

    static {
        STATUS_MAP.put("Đã đặt", OrderStatus.PLACED);
        STATUS_MAP.put("Đang xử lý", OrderStatus.PROCESSING);
        STATUS_MAP.put("Đã giao", OrderStatus.DELIVERED);
        STATUS_MAP.put("Đã hủy", OrderStatus.CANCELLED);

        PAYMENT_MAP.put("Thanh toán tiền mặt", PaymentMethod.CASH);
        PAYMENT_MAP.put("Chuyển khoản ngân hàng", PaymentMethod.BANK_TRANSFER);
    }

    public OrderSellerAdapter(Context context, List<Order> orderList, OrderDao orderDao, UserDao userDao) {
        this.context = context;
        this.orderList = orderList;
        this.orderDao = orderDao;
        this.userDao = userDao;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_seller_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Hiển thị thông tin cơ bản
        holder.orderIdText.setText("Mã đơn hàng: " + order.getOrderId());
        holder.orderDateText.setText("Ngày: " + formatDate(order.getCreatedAt()));
        holder.customerIdText.setText("Mã khách hàng: " + (order.getCustomerId() != null ? order.getCustomerId() : "N/A"));
        holder.deliveryAddressText.setText("Địa chỉ: " + (order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "Chưa cập nhật"));
        holder.totalAmountText.setText("Tổng tiền: $" + order.getTotalAmount());

        // Lấy tên khách hàng từ UserDao
        new Thread(() -> {
            Integer customerId = order.getCustomerId();
            String customerName = "Không rõ";
            if (customerId != null) {
                User user = userDao.getUserById(customerId).getValue();
                if (user != null) {
                    customerName = user.getFullName() != null ? user.getFullName() : "Không rõ";
                }
            }
            final String finalCustomerName = customerName;
            ((Activity) context).runOnUiThread(() -> {
                holder.customerNameText.setText("Tên khách hàng: " + finalCustomerName);
            });
        }).start();

        // Cấu hình Spinner cho OrderStatus
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                context, R.array.order_status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.statusSpinner.setAdapter(statusAdapter);
        holder.statusSpinner.setSelection(getIndex(statusAdapter, getVietnameseStatus(order.getStatus().name())));

        // Cấu hình Spinner cho PaymentMethod
        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(
                context, R.array.payment_method_array, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.paymentMethodSpinner.setAdapter(paymentAdapter);
        holder.paymentMethodSpinner.setSelection(getIndex(paymentAdapter, getVietnamesePayment(order.getPaymentMethod().name())));

        // Xử lý nút Lưu
        holder.saveButton.setOnClickListener(v -> {
            String newStatusVietnamese = holder.statusSpinner.getSelectedItem().toString();
            String newPaymentMethodVietnamese = holder.paymentMethodSpinner.getSelectedItem().toString();

            OrderStatus newStatus = STATUS_MAP.get(newStatusVietnamese);
            PaymentMethod newPaymentMethod = PAYMENT_MAP.get(newPaymentMethodVietnamese);

            if (newStatus != null && newPaymentMethod != null) {
                order.setStatus(newStatus);
                order.setPaymentMethod(newPaymentMethod);
                order.setUpdatedAt(System.currentTimeMillis());

                new Thread(() -> {
                    orderDao.updateOrder(order);
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            } else {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Xử lý nút Xem chi tiết
        holder.viewDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailSellerActivity.class);
            intent.putExtra("orderId", order.getOrderId());
            context.startActivity(intent);
        });
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }

    private int getIndex(ArrayAdapter<CharSequence> adapter, String item) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0;
    }

    private String getVietnameseStatus(String englishStatus) {
        for (Map.Entry<String, OrderStatus> entry : STATUS_MAP.entrySet()) {
            if (entry.getValue().name().equals(englishStatus)) {
                return entry.getKey();
            }
        }
        return "Đã đặt";
    }

    private String getVietnamesePayment(String englishPayment) {
        for (Map.Entry<String, PaymentMethod> entry : PAYMENT_MAP.entrySet()) {
            if (entry.getValue().name().equals(englishPayment)) {
                return entry.getKey();
            }
        }
        return "Thanh toán tiền mặt";
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, orderDateText, customerIdText, customerNameText, deliveryAddressText, totalAmountText;
        Spinner statusSpinner, paymentMethodSpinner;
        Button saveButton, viewDetailsButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.order_id_text);
            orderDateText = itemView.findViewById(R.id.order_date_text);
            customerIdText = itemView.findViewById(R.id.customer_id_text);
            customerNameText = itemView.findViewById(R.id.customer_name_text);
            deliveryAddressText = itemView.findViewById(R.id.delivery_address_text);
            totalAmountText = itemView.findViewById(R.id.total_amount_text);
            statusSpinner = itemView.findViewById(R.id.status_spinner);
            paymentMethodSpinner = itemView.findViewById(R.id.payment_method_spinner);
            saveButton = itemView.findViewById(R.id.save_button);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
        }
    }
}