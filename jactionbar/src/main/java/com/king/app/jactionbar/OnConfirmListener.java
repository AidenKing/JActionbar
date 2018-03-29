package com.king.app.jactionbar;

/**
 * 描述:confirm/cancel event
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/28 16:45
 */
public interface OnConfirmListener {

    /**
     *
     * @return if true, confirm status will not disappear even if onConfirm return true
     */
    boolean disableInstantDismissConfirm();

    /**
     *
     * @return if true, confirm status will not disappear even if onConfirm return true
     */
    boolean disableInstantDismissCancel();

    boolean onConfirm(int actionId);

    boolean onCancel(int actionId);
}
