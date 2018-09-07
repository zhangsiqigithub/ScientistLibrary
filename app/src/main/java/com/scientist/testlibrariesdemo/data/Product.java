package com.scientist.testlibrariesdemo.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import java.lang.reflect.Field;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/7/18
 * Time: 18:49
 * Desc:
 */
@Entity(indices = {@Index(value = "productId", unique = true)})
public class Product {

    public static final DiffUtil.ItemCallback<Product> DIFFCALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(Product oldItem, Product newItem) {
            return oldItem.getProductId() == newItem.getProductId();
        }

        @Override
        public boolean areContentsTheSame(Product oldItem, Product newItem) {
            return false;
        }
    };

    private String productName;

    @PrimaryKey
    @NonNull
    private long productId;

    private double price;

    public Product(String productName, long productId, double price) {
        this.productName = productName;
        this.productId = productId;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getProductId() {
        return productId;
    }

    public double getPrice() {
        return price;
    }



    @Override
    public String toString() {
        try {
            Field[] fields = getClass().getDeclaredFields();
            StringBuilder stringBuilder = new StringBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                stringBuilder.append(field.getName());
                stringBuilder.append(":");
                stringBuilder.append(field.get(this));
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
