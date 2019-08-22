package com.king.lib.jactionbar;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/3/25 0025 21:06
 */

public abstract class SelectableAdapter<V extends ViewDataBinding, T> extends BaseRecyclerAdapter<V, T> implements View.OnClickListener {

    protected boolean isSelect;

    protected SparseBooleanArray checkMap;

    private String mKeyword;

    private List<T> originList;

    public SelectableAdapter() {
        checkMap = new SparseBooleanArray();
    }

    @Override
    public void setList(@NonNull List<T> data) {
        originList = data;
        list = new ArrayList<>();
        for (T t:originList) {
            list.add(t);
        }
    }

    public void setSelect(boolean select) {
        this.isSelect = select;
    }

    public void swapData(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        }
        else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (isSelect) {
            checkMap.put(position, !checkMap.get(position));
            notifyItemChanged(position);
        }
        else {
            if (onItemClickListener != null) {
                onItemClickListener.onClickItem(position, list.get(position));
            }
        }
    }

    public List<T> getSelectedData() {
        List<T> stories = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i ++) {
            if (checkMap.get(i)) {
                stories.add(list.get(i));
            }
        }
        return stories;
    }

    @Override
    protected void onBindItem(V holder, int position, T bean) {
        getCheckBox(holder).setVisibility(isSelect ? View.VISIBLE:View.GONE);
        getCheckBox(holder).setChecked(checkMap.get(position));

        getGroupItem(holder).setTag(position);
        getGroupItem(holder).setOnClickListener(this);
        onBindSubHolder(holder, position, bean);
    }

    protected abstract ViewGroup getGroupItem(V holder);

    protected abstract CheckBox getCheckBox(V holder);

    protected abstract void onBindSubHolder(V holder, int position, T bean);

    public void filter(String text) {
        if (!text.equals(mKeyword)) {
            list.clear();
            mKeyword = text;
            for (int i = 0; i < originList.size(); i ++) {
                if (TextUtils.isEmpty(text)) {
                    list.add(originList.get(i));
                }
                else {
                    if (isMatchForKeyword(originList.get(i), text)) {
                        list.add(originList.get(i));
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    protected abstract boolean isMatchForKeyword(T t, String text);

    public void selectAll(boolean select) {
        for (int i = 0; i < getItemCount(); i ++) {
            checkMap.put(i, select);
        }
        notifyDataSetChanged();
    }
}
