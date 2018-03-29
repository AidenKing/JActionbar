package com.king.lib.jactionbar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/29 16:27
 */
public class SampleAdapter extends SelectableAdapter<SampleAdapter.SampleHolder, String> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_sample;
    }

    @Override
    protected SampleHolder newViewHolder(View view) {
        return new SampleHolder(view);
    }

    @Override
    protected ViewGroup getGroupItem(SampleHolder holder) {
        return holder.groupItem;
    }

    @Override
    protected CheckBox getCheckBox(SampleHolder holder) {
        return holder.cbCheck;
    }

    @Override
    protected void onBindSubHolder(SampleHolder holder, int position) {
        holder.tvName.setText(list.get(position));
    }

    @Override
    protected boolean isMatchForKeyword(String s, String text) {
        return s.toLowerCase().contains(text.toLowerCase());
    }

    public static class SampleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.cb_check)
        CheckBox cbCheck;
        @BindView(R.id.group_item)
        LinearLayout groupItem;

        public SampleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
