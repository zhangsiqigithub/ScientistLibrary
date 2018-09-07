package com.scientist.testlibrariesdemo;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scientist.testlibrariesdemo.data.Product;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/7/18
 * Time: 19:23
 * Desc:
 */
public class ProductPagedListAdapter extends PagedListAdapter<Product, ProductPagedListAdapter.ViewHolder> {

    protected ProductPagedListAdapter() {
        super(Product.DIFFCALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paging, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = getItem(position);
        if (product == null) {
            holder.clear();
        } else {
            holder.bind(product);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView idText;
        TextView nameText;
        TextView priceText;

        public ViewHolder(View itemView) {
            super(itemView);
            idText = itemView.findViewById(R.id.tv_id);
            nameText = itemView.findViewById(R.id.tv_name);
            priceText = itemView.findViewById(R.id.tv_price);
        }

        void clear() {
            idText.setText("id clean");
            nameText.setText("name clean");
            priceText.setText("price clean");
        }

        void bind(Product product) {
            idText.setText("id " + product.getProductId());
            nameText.setText(product.getProductName());
            priceText.setText(String.valueOf(product.getPrice()));
        }

    }
}
