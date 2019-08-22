package com.king.lib.jactionbar;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/14 15:12
 */
public abstract class BaseRecyclerAdapter<V extends ViewDataBinding, T> extends RecyclerView.Adapter {

    protected List<T> list;

    protected OnItemClickListener<T> onItemClickListener;

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        V binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , getItemLayoutRes(), parent, false);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        return holder;
    }

    protected abstract int getItemLayoutRes();

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        V binding = getBindingFromHolder(holder);
        onBindItem(binding, position, list.get(position));
        binding.executePendingBindings();
    }

    protected V getBindingFromHolder(RecyclerView.ViewHolder holder) {
        return DataBindingUtil.getBinding(holder.itemView);
    }

    protected abstract void onBindItem(V binding, int position, T bean);

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public interface OnItemClickListener<T> {
        void onClickItem(int position, T data);
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {

        public BindingHolder(View itemView) {
            super(itemView);
        }
    }
}
