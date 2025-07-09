package com.example.holafood.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.R;
import com.example.holafood.dao.ProductDao;
import com.example.holafood.model.OrderItem;
import com.example.holafood.model.Product;

import java.io.File;
import java.util.List;

public class OrderItemSellerAdapter extends RecyclerView.Adapter<OrderItemSellerAdapter.OrderItemViewHolder> {

    private static final String TAG = "OrderItemSellerAdapter";
    private List<OrderItem> orderItemList;
    private Context context;
    private ProductDao productDao;

    public OrderItemSellerAdapter(Context context, List<OrderItem> orderItemList, ProductDao productDao) {
        this.context = context;
        this.orderItemList = orderItemList;
        this.productDao = productDao;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_detail_seller_layout, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItemList.get(position);

        new Thread(() -> {
            if (item.getProductId() != null) {
                Product product = productDao.getProductById(item.getProductId()).getValue();
                ((Activity) context).runOnUiThread(() -> {
                    if (product != null) {
                        holder.productNameText.setText("Tên sản phẩm: " + (product.getName() != null ? product.getName() : "Không rõ"));
                        String imagePath = product.getImagePath();
                        if (imagePath != null && !imagePath.isEmpty()) {
                            Log.d(TAG, "Attempting to load image from: " + imagePath);
                            try {
                                Bitmap bitmap = BitmapFactory.decodeFile(getFullImagePath(imagePath));
                                if (bitmap != null) {
                                    holder.productImageView.setImageBitmap(bitmap);
                                } else {
                                    Log.w(TAG, "Failed to decode bitmap for path: " + imagePath);
                                    holder.productImageView.setImageResource(R.drawable.default_image);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error loading image: " + e.getMessage());
                                holder.productImageView.setImageResource(R.drawable.default_image);
                            }
                        } else {
                            Log.w(TAG, "Image path is null or empty");
                            holder.productImageView.setImageResource(R.drawable.default_image);
                        }
                    } else {
                        Log.w(TAG, "Product not found for productId: " + item.getProductId());
                        holder.productNameText.setText("Tên sản phẩm: Không rõ");
                        holder.productImageView.setImageResource(R.drawable.default_image);
                    }
                    holder.quantityText.setText("Số lượng: " + item.getQuantity());
                    holder.unitPriceText.setText("Đơn giá: $" + item.getUnitPrice());
                });
            }
        }).start();
    }

    private String getFullImagePath(String imageName) {
        // Giả định hình ảnh nằm trong thư mục pictures của bộ nhớ ngoài
        File directory = new File("/storage/emulated/0/pictures/");
        return new File(directory, imageName).getAbsolutePath();
    }

    @Override
    public int getItemCount() {
        return orderItemList != null ? orderItemList.size() : 0;
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView productNameText, quantityText, unitPriceText;
        ImageView productImageView;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameText = itemView.findViewById(R.id.product_name_text);
            productImageView = itemView.findViewById(R.id.product_image_view);
            quantityText = itemView.findViewById(R.id.quantity_text);
            unitPriceText = itemView.findViewById(R.id.unit_price_text);
        }
    }
}