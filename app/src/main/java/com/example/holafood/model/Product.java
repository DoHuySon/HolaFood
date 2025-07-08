package com.example.holafood.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Products",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "seller_id",
                onDelete = ForeignKey.CASCADE))
public class Product {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private int productId;

    @ColumnInfo(name = "seller_id")
    private int sellerId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "image_path", typeAffinity = ColumnInfo.TEXT, defaultValue = "")
    private String imagePath; // Thay image_resource_name bằng image_path

    @ColumnInfo(name = "status")
    private ProductStatus status;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public Product() {
        this.status = ProductStatus.AVAILABLE;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Product(int sellerId, String name, String description, double price, String imagePath, ProductStatus status) {
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath; // Sử dụng imagePath
        this.status = status != null ? status : ProductStatus.AVAILABLE;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getter và Setter
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImagePath() { return imagePath; } // Thay getImageResourceName
    public void setImagePath(String imagePath) { this.imagePath = imagePath; } // Thay setImageResourceName
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}