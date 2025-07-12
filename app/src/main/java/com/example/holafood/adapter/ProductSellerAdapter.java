package com.example.holafood.adapter;

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

import com.example.holafood.R;
import com.example.holafood.model.Product;
import com.example.holafood.model.ProductStatus;

import java.util.List;

public class ProductSellerAdapter extends RecyclerView.Adapter<ProductSellerAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(int productId);
    }

    public ProductSellerAdapter(List<Product> productList, OnProductActionListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_seller, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textProductName.setText(product.getName());
        holder.textProductPrice.setText("Giá: " + product.getPrice());
        holder.textProductStatus.setText("Trạng thái: " + product.getStatus().getDisplayName());
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
        holder.buttonEdit.setOnClickListener(v -> listener.onEditProduct(product));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteProduct(product.getProductId()));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textProductName, textProductPrice, textProductStatus;
        Button buttonEdit, buttonDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textProductStatus = itemView.findViewById(R.id.text_product_status);
            buttonEdit = itemView.findViewById(R.id.button_edit_product);
            buttonDelete = itemView.findViewById(R.id.button_delete_product);
        }
    }
}