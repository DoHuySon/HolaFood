package com.example.holafood.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
import java.util.concurrent.Executors;

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

    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItemList.get(position);

        if (item.getProductId() != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Log.d(TAG, "Loading product details for productId: " + item.getProductId());
                Product product = productDao.getProductByIdSync(item.getProductId());
                ((Activity) context).runOnUiThread(() -> {
                    if (product != null) {
                        holder.productNameText.setText("Tên sản phẩm: " + (product.getName() != null ? product.getName() : "Không rõ"));
                        String imagePath = product.getImagePath();
                        Log.d(TAG, "Image path from product: " + imagePath);
                        if (imagePath != null && !imagePath.isEmpty()) {
                            String fullPath = getFullImagePath(imagePath);
                            if (fullPath != null) {
                                Bitmap bitmap = BitmapFactory.decodeFile(fullPath);
                                if (bitmap != null) {
                                    Log.d(TAG, "Image decoded successfully from: " + fullPath);
                                    holder.productImageView.setImageBitmap(bitmap);
                                } else {
                                    Log.w(TAG, "Failed to decode bitmap from: " + fullPath);
                                    holder.productImageView.setImageResource(R.drawable.default_image);
                                }
                            } else {
                                Log.w(TAG, "Full path is null for image: " + imagePath);
                                holder.productImageView.setImageResource(R.drawable.default_image);
                            }
                        } else {
                            Log.w(TAG, "Image path is null or empty for productId: " + item.getProductId());
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
            });
        }
    }

    private String getFullImagePath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            Log.w(TAG, "Image path is null or empty");
            return null;
        }

        // Kiểm tra nếu là đường dẫn nội bộ (bắt đầu bằng /data/user/0/)
        if (imagePath.startsWith("/data/user/0/")) {
            File file = new File(imagePath);
            if (file.exists()) {
                Log.d(TAG, "Internal image found at: " + file.getAbsolutePath());
                return file.getAbsolutePath();
            } else {
                Log.e(TAG, "Internal image not found at: " + file.getAbsolutePath());
                return null;
            }
        }

        // Nếu không phải đường dẫn nội bộ, thử tìm ở thư mục công cộng
        if (!imagePath.contains(".")) {
            String[] possibleExtensions = {".jpg", ".png", ".jpeg"};
            for (String ext : possibleExtensions) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imagePath + ext);
                if (file.exists()) {
                    Log.d(TAG, "Public image found with extension: " + imagePath + ext);
                    return file.getAbsolutePath();
                }
            }
            Log.w(TAG, "No matching public file found for: " + imagePath);
            return null;
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imagePath);
        if (file.exists()) {
            Log.d(TAG, "Public image found at: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } else {
            Log.e(TAG, "Public image not found at: " + file.getAbsolutePath());
            return null;
        }
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