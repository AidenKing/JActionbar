package com.king.app.jactionbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.king.app.jactionbar.conf.Constants;
import com.king.app.jactionbar.parser.MenuParser;
import com.king.app.jactionbar.utils.ParamUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * customize actionbar extended from RelativeLayout
 * support parse android menu resource to action icons and menu items
 * support set icon's size and padding, control the maximum icons to show
 * integrate search icon and group, and expose event for words filter
 * integrate ok/cancel group and expose event listener
 * <p/>author：Aiden
 * <p/>create time: 2018/3/27 16:10
 */
public class JActionbar extends RelativeLayout {

    /**
     * the ids of integrated views
     */
    private final int ID_GROUP_TITLE = 1;
    private final int ID_GROUP_ICON = 2;
    private final int ID_ICON_SEARCH = 3;
    private final int ID_ICON_BACK = 4;
    private final int ID_ICON_MORE = 5;
    private final int ID_ICON_CLOSE_SEARCH = 6;
    private final int ID_CONFIRM_OK = 7;
    private final int ID_CONFIRM_CANCEL = 8;

    /**
     * icon size
     */
    private int iconSize = ParamUtils.dp2px(40);

    /**
     * icon padding
     */
    private int iconPadding = ParamUtils.dp2px(8);

    /**
     * icon color
     */
    private int iconColor = Color.WHITE;

    /**
     * support search icon and search input event
     */
    private boolean isSupportSearch = false;

    /**
     * ripple background color of icon
     */
    private int rippleColor = Color.parseColor("#66000000");

    /**
     * background of actionbar
     */
    private int backgroundColor;

    /**
     * max icon numbers to show
     */
    private int maxShowIcon = 4;

    /**
     * back
     */
    private ImageView ivBack;

    /**
     * more menu icon
     */
    private ImageView ivMenu;

    /**
     * title
     */
    private TextView tvTitle;

    /**
     * the group of all icons generated by menu
     */
    private LinearLayout groupMenu;

    /**
     * the group of confirm cancel or ok
     */
    private LinearLayout groupConfirm;

    /**
     * menu popup
     */
    private ListPopupWindow mPopup;

    /**
     * the group of search
     */
    private RelativeLayout groupSearch;

    /**
     * search icon
     * could only be shown at the most right or next most right
     */
    private ImageView ivSearch;

    /**
     * close search icon
     */
    private ImageView ivCloseSearch;

    /**
     * the action event of search keywords
     */
    private OnSearchListener onSearchListener;

    /**
     * the action event of each icon generated by menu item
     */
    private OnMenuItemListener onMenuItemListener;

    /**
     * the action event of back icon
     */
    private OnBackListener onBackListener;

    /**
     * the specific action id of confirm event
     */
    private int mConfirmActionId;

    private OnConfirmListener onConfirmListener;

    /**
     * provider popup menu for icon menu
     */
    private PopupMenuProvider popupMenuProvider;

    /**
     * To change the color of EditText's underline:
     * set android:theme="@style/EtActionSearch" like below to <JActionbarView> tag in xml
     *     <style name="EtActionSearch" parent="Theme.AppCompat.Light.NoActionBar">
     *         <item name="colorControlNormal">@color/white</item>
     *         <item name="colorControlActivated">@color/white</item>
     *     </style>
     */
    private EditText etSearch;

    /**
     * Keep the data parsed from menu resource
     */
    private JMenu menu;

    /**
     * all menu items could be popped up as list
     */
    private List<JMenuItem> moreItemList;

    /**
     * menu items displayed eventually
     */
    private List<JMenuItem> displayMoreItemList;

    /**
     * adapter for popup menu items
     */
    private ArrayAdapter<JMenuItem> moreMenuAdapter;

    /**
     * registered popup menu for icons from outside
     */
    private Map<Integer, Boolean> popupMenuMap;

    /**
     * text color
     */
    private int titleColor;

    public JActionbar(Context context) {
        super(context);
        init(null);
    }

    public JActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        popupMenuMap = new HashMap<>();

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.JActionbar);
        rippleColor = typedArray.getColor(R.styleable.JActionbar_rippleColor, Color.parseColor("#66000000"));
        iconSize = typedArray.getDimensionPixelOffset(R.styleable.JActionbar_iconSize, ParamUtils.dp2px(40));
        iconPadding = typedArray.getDimensionPixelOffset(R.styleable.JActionbar_iconPadding, ParamUtils.dp2px(8));
        iconColor = typedArray.getColor(R.styleable.JActionbar_iconColor, Color.WHITE);
        isSupportSearch = typedArray.getBoolean(R.styleable.JActionbar_supportSearch, false);
        backgroundColor = typedArray.getColor(R.styleable.JActionbar_android_background, Color.parseColor("#fa7198"));
        maxShowIcon = typedArray.getInt(R.styleable.JActionbar_maxShowIcon, 4);

        initTitle(typedArray);
        initMenu(typedArray);
    }

    /**
     * group title: include back icon and title text
     * @param a
     */
    private void initTitle(TypedArray a) {
        boolean showBack = a.getBoolean(R.styleable.JActionbar_showIconBack, true);
        String title = a.getString(R.styleable.JActionbar_title);
        int titleSize = a.getDimensionPixelOffset(R.styleable.JActionbar_titleSize, ParamUtils.dp2px(18));

        addTitle();
        ivBack.setVisibility(showBack ? VISIBLE:GONE);
        if (!showBack) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
            params.leftMargin = ParamUtils.dp2px(16);
            tvTitle.setLayoutParams(params);
        }
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        tvTitle.setText(title);
        tvTitle.setTextAppearance(getContext(), R.style.TvTitle);

        titleColor = a.getColor(R.styleable.JActionbar_titleColor, Color.WHITE);
        tvTitle.setTextColor(titleColor);
    }

    /**
     * group menu: include all icons generated by menu resource
     * @param a
     */
    private void initMenu(TypedArray a) {
        groupMenu = new LinearLayout(getContext());
        groupMenu.setId(ID_GROUP_ICON);
        groupMenu.setOrientation(LinearLayout.HORIZONTAL);
        groupMenu.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.RIGHT_OF, ID_GROUP_TITLE);
        addView(groupMenu, params);

        int menuRes = a.getResourceId(R.styleable.JActionbar_menu, -1);
        if (menuRes != -1) {
            inflateMenu(menuRes);
        }
        else {
            showMenu();
        }
    }

    /**
     * add child views of title group
     */
    private void addTitle() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setId(ID_GROUP_TITLE);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        JMenuItem backItem = new JMenuItem();
        backItem.setId(ID_ICON_BACK);
        backItem.setIconRes(R.drawable.ic_back_white);
        ivBack = addIcon(backItem);
        ivBack.setOnClickListener(iconClickListener);
        layout.addView(ivBack);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvTitle = new TextView(getContext());
        tvTitle.setLayoutParams(params);
        tvTitle.setTextColor(Color.WHITE);
        layout.addView(tvTitle);

        LayoutParams rParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        addView(layout, rParams);
    }

    /**
     * inflate menu from menu resource
     * @param menuRes
     */
    public void inflateMenu(int menuRes) {
        // 必须重置view的状态，否则会影响后续判断（比如ivMenu是否为null会影响search动画）
        resetAllMenus();
        try {
            menu = new MenuParser().inflate(getContext(), menuRes);
            showMenu();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetAllMenus() {
        groupMenu.removeAllViews();
        ivSearch = null;
        ivMenu = null;
    }

    /**
     * 1. the maximum icons to show are decided by maxShowIcon, others will be presented as popup list items
     * 2. if isSupportSearch is true, then
     *      if total icons > maxShowIcon, search icon will display on the left of icon more
     *      else, search icon will display on the most right
     */
    private void showMenu() {
        int iconCount = 0;

        // never parsed xml or parsed with error
        if (menu == null) {
            menu = new JMenu();
        }

        List<JMenuItem> iconList = new ArrayList<>();
        moreItemList = new ArrayList<>();
        if (menu.getItemList() != null) {
            List<JMenuItem> list = menu.getItemList();
            for (JMenuItem item:list) {
                // as popup item
                if (item.getShowAsAction() == Constants.SHOW_AS_ACTION_NEVER) {
                    moreItemList.add(item);
                }
                else {
                    iconList.add(item);
                }
            }
            iconCount += iconList.size();
        }

        // decide whether to add search icon
        JMenuItem searchIcon = null;
        if (isSupportSearch) {
            iconCount ++;
            searchIcon = new JMenuItem();
            searchIcon.setId(ID_ICON_SEARCH);
            searchIcon.setIconRes(R.drawable.ic_search_white);
            searchIcon.setTitle("Search");
            initSearch();
        }

        // decide which place should search icon be put in
        if (iconCount > maxShowIcon) {
            JMenuItem menuIcon = new JMenuItem();
            menuIcon.setId(ID_ICON_MORE);
            menuIcon.setIconRes(R.drawable.ic_more_vert_white);
            menuIcon.setTitle("Menu");
            if (searchIcon != null) {
                iconList.add(maxShowIcon - 2, searchIcon);
            }
            iconList.add(maxShowIcon - 1, menuIcon);
        }
        else {
            if (searchIcon != null) {
                iconList.add(searchIcon);
            }
        }

        // decide how many icons should be shown
        for (int i = 0; i < maxShowIcon && i < iconCount; i ++) {
            JMenuItem item = iconList.get(i);
            ImageView view = addIcon(item);
            if (view.getId() == ID_ICON_SEARCH) {
                ivSearch = view;
            }
            else if (view.getId() == ID_ICON_MORE) {
                ivMenu = view;
            }
            view.setOnClickListener(iconClickListener);
            groupMenu.addView(view);
        }
        // the left items need be shown as menu item
        if (iconCount > maxShowIcon) {
            for (int i = maxShowIcon; i < iconList.size(); i ++) {
                moreItemList.add(iconList.get(i));
            }
        }

        if (ivMenu == null && moreItemList.size() > 0) {
            JMenuItem menuIcon = new JMenuItem();
            menuIcon.setId(ID_ICON_MORE);
            menuIcon.setIconRes(R.drawable.ic_more_vert_white);
            menuIcon.setTitle("Menu");
            ivMenu = addIcon(menuIcon);
            ivMenu.setOnClickListener(iconClickListener);
            groupMenu.addView(ivMenu);
        }

        // popup list to show menu item
        if (moreItemList.size() > 0) {
            displayMoreItemList = new ArrayList<>();
            for (JMenuItem item:moreItemList) {
                if (item.isVisible()) {
                    displayMoreItemList.add(item);
                }
            }

            moreMenuAdapter = new ArrayAdapter<JMenuItem>(getContext(), android.R.layout.simple_spinner_dropdown_item, displayMoreItemList) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ((TextView) view.findViewById(android.R.id.text1)).setText(displayMoreItemList.get(position).getTitle());
                    return view;
                }
            };
            mPopup = new ListPopupWindow(getContext());
            mPopup.setAnchorView(ivMenu);
            mPopup.setAdapter(moreMenuAdapter);
            mPopup.setWidth(ParamUtils.dp2px(140));
            mPopup.setHeight(ListPopupWindow.WRAP_CONTENT);
            mPopup.setHorizontalOffset(-10);
            mPopup.setDropDownGravity(Gravity.END);
            mPopup.setModal(true);
            mPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (onMenuItemListener != null) {
                        onMenuItemListener.onMenuItemSelected(displayMoreItemList.get(position).getId());
                    }
                    mPopup.dismiss();
                }
            });
        }
    }

    /**
     * add child views of search group: include EditText and close icon
     */
    private void initSearch() {
        groupSearch = new RelativeLayout(getContext());
        groupSearch.setVisibility(GONE);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        addView(groupSearch, params);

        etSearch = new EditText(getContext());
        etSearch.setTextColor(titleColor);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.leftMargin = ParamUtils.dp2px(16);
        params.rightMargin = ParamUtils.dp2px(10);
        params.bottomMargin = ParamUtils.dp2px(5);
        etSearch.setLayoutParams(params);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (onSearchListener != null) {
                    onSearchListener.onSearchWordsChanged(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        groupSearch.addView(etSearch);

        JMenuItem item = new JMenuItem();
        item.setId(ID_ICON_CLOSE_SEARCH);
        item.setIconRes(R.drawable.ic_close_white);
        params = new LayoutParams(iconSize, iconSize);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        ivCloseSearch = addIcon(item, params);
        ivCloseSearch.setOnClickListener(iconClickListener);
        groupSearch.addView(ivCloseSearch);

        // make sure getWidth() is not 0
        post(new Runnable() {
            @Override
            public void run() {
                LayoutParams params = (LayoutParams) groupSearch.getLayoutParams();
                params.width = getWidth() - iconSize;
                groupSearch.setLayoutParams(params);
            }
        });
    }

    private ImageView addIcon(JMenuItem item) {
        return addIcon(item, null);
    }

    /**
     * control the style of all icons
     * @param item
     * @param params
     * @return
     */
    private ImageView addIcon(JMenuItem item, LayoutParams params) {
        ImageView view = new ImageView(getContext());
        view.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.TRANSPARENT);
        // param1:ripple color, param2:background of normal status, param3:the limit of ripple
        RippleDrawable drawable = new RippleDrawable(ColorStateList.valueOf(rippleColor)
                , gd, new ShapeDrawable(new OvalShape()));
        view.setBackground(drawable);

        view.setId(item.getId());
        view.setTag(item);
        view.setImageResource(item.getIconRes());
        if (params == null) {
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(iconSize, iconSize);
            view.setLayoutParams(lParams);
        }
        else {
            view.setLayoutParams(params);
        }

        // 这里用SRC_IN，第一个参数是source，表示source与原图像叠加后相交的部分运用source的颜色，如果是SRC_TOP则是叠加的情况，SRC则是source color充满整个view区域
        view.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        return view;
    }

    /**
     * show cancel/ok actions
     * @param actionId
     */
    public void showConfirmStatus(int actionId) {
        mConfirmActionId = actionId;
        if (groupConfirm == null) {
            initGroupConfirm();
        }
        else {
            groupConfirm.setVisibility(VISIBLE);
        }
        groupMenu.setVisibility(GONE);
    }

    public void cancelConfirmStatus() {
        mConfirmActionId = 0;
        groupConfirm.setVisibility(GONE);
        groupMenu.setVisibility(VISIBLE);
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    private void initGroupConfirm() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        groupConfirm = new LinearLayout(getContext());
        groupConfirm.setLayoutParams(params);

        TextView view = addConfirmText(ID_CONFIRM_CANCEL, "Cancel");
        groupConfirm.addView(view);
        view = addConfirmText(ID_CONFIRM_OK, "Ok");
        groupConfirm.addView(view);

        addView(groupConfirm);
    }

    private TextView addConfirmText(int id, String text) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView view = new TextView(getContext());
        view.setText(text);
        view.setId(id);
        view.setPadding(ParamUtils.dp2px(15), 0, ParamUtils.dp2px(15), 0);
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, ParamUtils.dp2px(18));
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.TRANSPARENT);
        // param1:ripple color, param2:background of normal status, param3:the limit of ripple
        RippleDrawable drawable = new RippleDrawable(ColorStateList.valueOf(rippleColor)
                , gd, new ShapeDrawable(new RectShape()));
        view.setBackground(drawable);
        view.setOnClickListener(iconClickListener);
        view.setLayoutParams(params);
        view.setTextAppearance(getContext(), R.style.TvTitle);
        return view;
    }

    private OnClickListener iconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case ID_ICON_BACK:
                    if (onBackListener != null) {
                        onBackListener.onBack();
                    }
                    break;
                case ID_ICON_SEARCH:
                    showSearchBar();
                    break;
                case ID_ICON_CLOSE_SEARCH:
                    hideSearchBar();
                    break;
                case ID_ICON_MORE:
                    mPopup.show();
                    break;
                case ID_CONFIRM_OK:
                    if (onConfirmListener == null) {
                        cancelConfirmStatus();
                    }
                    else {
                        if (onConfirmListener.onConfirm(mConfirmActionId)) {
                            if (!onConfirmListener.disableInstantDismissConfirm()) {
                                cancelConfirmStatus();
                            }
                        }
                    }
                    break;
                case ID_CONFIRM_CANCEL:
                    onCancel();
                    break;
                default:
                    // check if registered popup menu for icon and don't dispatch to menu item listener if registered
                    if (popupMenuMap.get(v.getId()) != null && popupMenuMap.get(v.getId())) {
                        if (popupMenuProvider != null) {
                            popupMenuProvider.getPopupMenu(v.getId(), v).show();
                            return;
                        }
                    }
                    if (onMenuItemListener != null) {
                        onMenuItemListener.onMenuItemSelected(v.getId());
                    }
                    break;
            }
        }
    };

    private void onCancel() {
        if (onConfirmListener == null) {
            cancelConfirmStatus();
        }
        else {
            if (onConfirmListener.onCancel(mConfirmActionId)) {
                if (!onConfirmListener.disableInstantDismissCancel()) {
                    cancelConfirmStatus();
                }
            }
        }
    }

    /**
     * search icon is in the most right
     * true: search group execute animation with alpha from 0 to 1
     * false: search group execute animation with alpha from 0 to 1 and follow search icon to move to the most right
     * search icon could only be in the most right or the next most right for now
     */
    private void showSearchBar() {
        if (groupSearch.getVisibility() != View.VISIBLE) {

            // hide all views except search icon and menu icon
            if (ivBack != null) {
                ivBack.setVisibility(GONE);
            }
            tvTitle.setVisibility(GONE);
            for (int i = 0; i < groupMenu.getChildCount(); i ++) {
                int id = groupMenu.getChildAt(i).getId();
                if (id != ID_ICON_SEARCH && id != ID_ICON_MORE) {
                    groupMenu.getChildAt(i).setVisibility(GONE);
                }
            }

            // show search group and animate search icon
            groupSearch.setVisibility(View.VISIBLE);
            if (ivMenu != null) {
                ivMenu.setVisibility(INVISIBLE);

                Animation iconAnim = new TranslateAnimation(0, ivSearch.getWidth(), 0, 0);
                iconAnim.setDuration(500);
                iconAnim.setFillAfter(true);
                ivSearch.startAnimation(iconAnim);

                AnimationSet set = new AnimationSet(true);
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                Animation alpha = new AlphaAnimation(0, 1);
                alpha.setDuration(500);
                set.addAnimation(alpha);
                int startWidth = getWidth() - iconSize * 2;
                final int targetWidth = getWidth() - iconSize;
                float fs = (float) startWidth / (float) targetWidth;
                Animation scale = new ScaleAnimation(fs, 1, 1, 1);
                scale.setDuration(500);
                set.addAnimation(scale);
                groupSearch.startAnimation(set);
            }
            else {
                Animation animation = new AlphaAnimation(0, 1);
                animation.setDuration(500);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                groupSearch.startAnimation(animation);
            }
        }
    }

    /**
     * search icon is in the most right
     * true: search group execute animation with alpha from 1 to 0
     * false: search group execute animation with alpha from 1 to 0 and search icon will move to the original place
     */
    private void hideSearchBar() {
        if (ivMenu != null) {
            Animation iconAnim = new TranslateAnimation(ivSearch.getWidth(), 0, 0, 0);
            iconAnim.setDuration(500);
            ivSearch.startAnimation(iconAnim);

            Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(500);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    groupSearch.setVisibility(View.GONE);

                    // show all views except search group
                    if (ivMenu != null) {
                        ivMenu.setVisibility(VISIBLE);
                    }
                    if (ivBack != null) {
                        ivBack.setVisibility(VISIBLE);
                    }
                    tvTitle.setVisibility(VISIBLE);
                    for (int i = 0; i < groupMenu.getChildCount(); i ++) {
                        int id = groupMenu.getChildAt(i).getId();
                        if (id != ID_ICON_SEARCH && id != ID_ICON_MORE) {
                            groupMenu.getChildAt(i).setVisibility(VISIBLE);
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            groupSearch.startAnimation(animation);
        }
        else {
            Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(500);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    groupSearch.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            groupSearch.startAnimation(animation);
        }
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    public void setOnMenuItemListener(OnMenuItemListener onMenuItemListener) {
        this.onMenuItemListener = onMenuItemListener;
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    public void setTitle(String name) {
        tvTitle.setText(name);
    }

    /**
     * need call this before inflate
     */
    public void enableSearch() {
        isSupportSearch = true;
    }

    /**
     * need call this before inflate
     */
    public void disableSearch() {
        isSupportSearch = false;
    }

    public void setPopupMenuProvider(PopupMenuProvider popupMenuProvider) {
        this.popupMenuProvider = popupMenuProvider;
    }

    /**
     * register PopupMenu for menu item(only worked for items presented by icon)
     * @param menuItemId
     */
    public void registerPopupMenu(int menuItemId) {
        popupMenuMap.put(menuItemId, true);
    }

    /**
     * cancel PopupMenu register for menu item
     * @param menuItemId
     */
    public void removeRegisteredPopupMenu(int menuItemId) {
        popupMenuMap.remove(menuItemId);
    }

    /**
     * update menu text by specific id
     * @param id
     * @param text
     */
    public void updateMenuText(int id, String text) {
        if (moreItemList != null) {
            for (JMenuItem item:moreItemList) {
                if (item.getId() == id) {
                    item.setTitle(text);
                    moreMenuAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    /**
     * update visibility for menu text by specific id
     * @param id
     * @param visible
     */
    public void updateMenuItemVisible(int id, boolean visible) {
        if (moreItemList != null) {
            displayMoreItemList.clear();
            for (JMenuItem item:moreItemList) {
                if (item.getId() == id) {
                    item.setVisible(visible);
                }
                if (item.isVisible()) {
                    displayMoreItemList.add(item);
                }
            }
            moreMenuAdapter.notifyDataSetChanged();
        }
    }

    /**
     * to control the confirm status
     * @return
     */
    public boolean onBackPressed() {
        if (mConfirmActionId != 0) {
            onCancel();
            return true;
        }
        return false;
    }
}
