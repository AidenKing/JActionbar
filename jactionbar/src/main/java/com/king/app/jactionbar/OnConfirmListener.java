package com.king.app.jactionbar;

/**
 * confirm/cancel event
 * <p/>author：Aiden
 * <p/>create time: 2018/3/28 16:45
 */
public interface OnConfirmListener {

    /**
     *
     * @return if true, you can manually call cancelConfirmStatus to make confirm group disappear
     */
    boolean disableInstantDismissConfirm();

    /**
     *
     * @return if true, you can manually call cancelConfirmStatus to make confirm group disappear
     */
    boolean disableInstantDismissCancel();

    boolean onConfirm(int actionId);

    boolean onCancel(int actionId);
}
