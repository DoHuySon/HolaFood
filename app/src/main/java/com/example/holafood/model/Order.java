package com.example.holafood.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Orders",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "customer_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "seller_id",
                        onDelete = ForeignKey.SET_NULL)
        })
public class Order {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_id")
    private int orderId;

    @ColumnInfo(name = "customer_id")
    private Integer customerId;

    @ColumnInfo(name = "seller_id")
    private Integer sellerId;

    @ColumnInfo(name = "total_amount")
    private double totalAmount;

    @ColumnInfo(name = "status")
    private OrderStatus status;

    @ColumnInfo(name = "delivery_address")
    private String deliveryAddress;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "payment_method")
    private PaymentMethod paymentMethod;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public Order() {
        this.status = OrderStatus.PLACED;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Order(Integer customerId, Integer sellerId, double totalAmount, String deliveryAddress,
                 String phoneNumber, PaymentMethod paymentMethod, OrderStatus status) {
        this.customerId = customerId;
        this.sellerId = sellerId;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.phoneNumber = phoneNumber;
        this.paymentMethod = paymentMethod;
        this.status = status != null ? status : OrderStatus.PLACED;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getter và Setter
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}

