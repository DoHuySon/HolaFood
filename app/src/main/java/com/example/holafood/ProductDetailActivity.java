package com.example.holafood;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.holafood.model.Product;


public class ProductDetailActivity extends AppCompatActivity {
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        product = (Product) getIntent().getSerializableExtra("product");

        if (product == null) {
            new Thread(() -> {
                product = App.getDatabase().productDao().getProductByIdNow(1);
                runOnUiThread(this::populateUI);
            }).start();
        } else {
            populateUI();
        }

        Button buttonOrder = findViewById(R.id.button_order);
        buttonOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });
    }

    private void populateUI() {
        if (product == null) return;

        TextView nameText = findViewById(R.id.product_name);
        TextView priceText = findViewById(R.id.product_price);
        TextView descriptionText = findViewById(R.id.product_description);
        ImageView productImage = findViewById(R.id.product_image);

        nameText.setText(product.getName());
        priceText.setText(product.getPrice() + "đ");
        descriptionText.setText(product.getDescription());

        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            int imageResId = getResources().getIdentifier(product.getImagePath(), "drawable", getPackageName());
            if (imageResId != 0) {
                productImage.setImageResource(imageResId);
            } else {
                productImage.setImageResource(R.drawable.default_food);
            }
        } else {
            productImage.setImageResource(R.drawable.default_food);
        }
    }
}
