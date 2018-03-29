package com.king.app.jactionbar;

import android.view.View;
import android.widget.PopupMenu;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/29 11:44
 */
public interface PopupMenuProvider {
    PopupMenu getPopupMenu(int iconMenuId, View anchorView);
}
