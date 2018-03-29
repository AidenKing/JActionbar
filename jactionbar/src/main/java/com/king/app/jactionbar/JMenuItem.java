package com.king.app.jactionbar;

/**
 * 描述:support to parse android:id/android:title/android:icon/app:showAsAction for now
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/28 9:05
 */
public class JMenuItem {

    private int id;

    private String title;

    private int iconRes;

    /**
     * see Constants.SHOW_AS_ACTION_**
     */
    private int showAsAction;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getShowAsAction() {
        return showAsAction;
    }

    public void setShowAsAction(int showAsAction) {
        this.showAsAction = showAsAction;
    }
}
