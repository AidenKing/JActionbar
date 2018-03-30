package com.king.app.jactionbar;

import android.view.View;
import android.widget.PopupMenu;

/**
 *
 * <p/>author：Aiden
 * <p/>create time: 2018/3/29 11:44
 */
public interface PopupMenuProvider {
    PopupMenu getPopupMenu(int iconMenuId, View anchorView);
}
