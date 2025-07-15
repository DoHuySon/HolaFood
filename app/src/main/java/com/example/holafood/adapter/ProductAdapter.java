package com.example.holafood.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holafood.ProductDetailActivity;
import com.example.holafood.R;
import com.example.holafood.model.Product;
import com.example.holafood.model.ProductStatus;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onViewDetailsClick(Product product);
    }

    public ProductAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textProductName.setText(product.getName());
        holder.textProductPrice.setText("Giá: " + product.getPrice() + " USD");
        String statusText = product.getStatus() != null ? product.getStatus().getDisplayName() : "Không xác định";
        holder.textProductStatus.setText("Trạng thái: " + statusText);

        // Xử lý hiển thị hình ảnh với kiểm tra null
        if (holder.imageView != null) {
            if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(product.getImagePath());
                if (bitmap != null) {
                    holder.imageView.setImageBitmap(bitmap);
                } else {
                    holder.imageView.setImageResource(R.drawable.default_image); // Hình mặc định nếu không load được
                }
            } else {
                holder.imageView.setImageResource(R.drawable.default_image); // Hình mặc định
            }
        }

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailsClick(product);
            }
            // Chuyển sang DetailActivity
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("productId", product.getProductId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textProductName, textProductPrice, textProductStatus;
        Button btnViewDetails;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textProductStatus = itemView.findViewById(R.id.text_product_status);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
    }
}