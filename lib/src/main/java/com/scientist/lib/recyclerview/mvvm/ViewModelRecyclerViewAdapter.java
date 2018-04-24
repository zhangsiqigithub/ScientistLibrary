package com.scientist.lib.recyclerview.mvvm;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/4/24
 * Time: 10:21
 * Desc: MVVM结构的RecyclerView adapter
 */
public class ViewModelRecyclerViewAdapter extends RecyclerView.Adapter<ViewModelRecyclerViewAdapter.ViewHolder>{

    private ObservableArrayList<ItemViewModel> mData;
    public ViewModelRecyclerViewAdapter(ObservableArrayList<ItemViewModel> data) {
        mData = data;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getLayoutId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemViewModel viewModel = mData.get(position);
        holder.getBinding().setVariable(viewModel.getVariableId(), viewModel);
        ItemViewModel.ExtraViewModel[] extraViewModels = viewModel.getExtraViewModels();
        if (extraViewModels != null) {
            for (ItemViewModel.ExtraViewModel extraViewModel : extraViewModels) {
                holder.getBinding().setVariable(extraViewModel.getVariableId(), extraViewModel.getViewModel());
            }
        }
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private ViewDataBinding mBinding;
        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public ViewDataBinding getBinding() {
            return mBinding;
        }
    }
}
