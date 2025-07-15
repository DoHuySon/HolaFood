package com.example.holafood.model;

public enum OrderStatus {
    PLACED, PROCESSING, DELIVERED, CANCELLED;

    public String getVietnameseName() {
        switch (this) {
            case PLACED:
                return "Đặt thành công";
            case PROCESSING:
                return "Đang xử lý";
            case DELIVERED:
                return "Đã giao";
            case CANCELLED:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }
}