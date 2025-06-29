package com.example.holafood.model;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    // Role
    @TypeConverter
    public static String fromRole(Role role) {
        return role == null ? null : role.name();
    }

    @TypeConverter
    public static Role toRole(String role) {
        return role == null ? null : Role.valueOf(role);
    }

    // StoreStatus
    @TypeConverter
    public static String fromStoreStatus(StoreStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static StoreStatus toStoreStatus(String status) {
        return status == null ? StoreStatus.PENDING : StoreStatus.valueOf(status);
    }

    // ProductStatus
    @TypeConverter
    public static String fromProductStatus(ProductStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static ProductStatus toProductStatus(String status) {
        return status == null ? ProductStatus.AVAILABLE : ProductStatus.valueOf(status);
    }

    // OrderStatus
    @TypeConverter
    public static String fromOrderStatus(OrderStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static OrderStatus toOrderStatus(String status) {
        return status == null ? OrderStatus.PLACED : OrderStatus.valueOf(status);
    }

    // PaymentMethod
    @TypeConverter
    public static String fromPaymentMethod(PaymentMethod method) {
        return method == null ? null : method.name();
    }

    @TypeConverter
    public static PaymentMethod toPaymentMethod(String method) {
        return method == null ? null : PaymentMethod.valueOf(method);
    }

    // Date
    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}
