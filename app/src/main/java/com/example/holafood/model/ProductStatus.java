package com.example.holafood.model;

public enum ProductStatus {
    AVAILABLE, UNAVAILABLE;
    public String getDisplayName() {
        switch (this) {
            case AVAILABLE:
                return "Có sẵn";
            case UNAVAILABLE:
                return "Không có sẵn";
            default:
                return name(); // Fallback to enum name if needed
        }
    }
}
