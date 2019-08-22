package com.king.lib.jactionbar;

import android.view.ViewGroup;
import android.widget.CheckBox;

import com.king.lib.jactionbar.databinding.AdapterSampleBinding;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/29 16:27
 */
public class SampleAdapter extends SelectableAdapter<AdapterSampleBinding, String> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_sample;
    }

    @Override
    protected ViewGroup getGroupItem(AdapterSampleBinding holder) {
        return holder.groupItem;
    }

    @Override
    protected CheckBox getCheckBox(AdapterSampleBinding holder) {
        return holder.cbCheck;
    }

    @Override
    protected void onBindSubHolder(AdapterSampleBinding holder, int position, String bean) {
        holder.tvName.setText(bean);
    }

    @Override
    protected boolean isMatchForKeyword(String s, String text) {
        return s.toLowerCase().contains(text.toLowerCase());
    }
}
