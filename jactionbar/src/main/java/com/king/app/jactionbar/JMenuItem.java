package com.king.app.jactionbar;

/**
 * support to parse android:id/android:title/android:icon/app:showAsAction for now
 * <p/>author：Aiden
 * <p/>create time: 2018/3/28 9:05
 */
public class JMenuItem {

    private int id;

    private String title;

    private int iconRes;

    /**
     * see Constants.SHOW_AS_ACTION_**
     */
    private int showAsAction;

    private boolean isVisible;

    public JMenuItem() {
        isVisible = true;
    }

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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
