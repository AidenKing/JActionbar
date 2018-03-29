package com.king.lib.jactionbar;

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
public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

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
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutRes(), parent, false);
        return newViewHolder(view);
    }

    protected abstract int getItemLayoutRes();

    protected abstract VH newViewHolder(View view);

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public interface OnItemClickListener<T> {
        void onClickItem(int position, T data);
    }
}
