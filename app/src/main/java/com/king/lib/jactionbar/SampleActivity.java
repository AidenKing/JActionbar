package com.king.lib.jactionbar;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.king.app.jactionbar.JActionbar;
import com.king.app.jactionbar.OnBackListener;
import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.jactionbar.OnMenuItemListener;
import com.king.app.jactionbar.OnSearchListener;
import com.king.app.jactionbar.PopupMenuProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SampleActivity extends AppCompatActivity {

    @BindView(R.id.action_bar)
    JActionbar actionBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private SampleAdapter adapter;

    private Random random = new Random();

    private List<String> itemList;

    private PopupMenu popSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setStatusBarColor(getResources().getColor(R.color.status_bar_bg));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);

        initActionbar();
        initList();
    }

    private void setStatusBarColor(int statusColor) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusColor);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    private void initActionbar() {
        // back icon
        actionBar.setOnBackListener(new OnBackListener() {
            @Override
            public void onBack() {
                finish();
            }
        });
        // search event, listen to the change of input words
        actionBar.setOnSearchListener(new OnSearchListener() {
            @Override
            public void onSearchWordsChanged(String words) {
                adapter.filter(words);
            }
        });
        // icon or menu item listener
        actionBar.setOnMenuItemListener(new OnMenuItemListener() {
            @Override
            public void onMenuItemSelected(int menuId) {
                SampleActivity.this.onMenuItemSelected(menuId);
            }
        });
        // register popup menu for sort icon
        actionBar.registerPopupMenu(R.id.menu_sort);
        actionBar.setPopupMenuProvider(new PopupMenuProvider() {
            @Override
            public PopupMenu getPopupMenu(int iconMenuId, View anchorView) {
                if (iconMenuId == R.id.menu_sort) {
                    return getSortPopup(anchorView);
                }
                return null;
            }
        });
        // confirm yes/no event
        actionBar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean disableInstantDismissConfirm() {
                return false;
            }

            @Override
            public boolean disableInstantDismissCancel() {
                return false;
            }

            @Override
            public boolean onConfirm(int actionId) {
                return onConfirmAction(actionId);
            }

            @Override
            public boolean onCancel(int actionId) {
                return onCancelAction(actionId);
            }
        });
        actionBar.setTitle("Avengers\nInfinity war");
    }

    private void initList() {

        itemList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            itemList.add(getRandomText() + i);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new SampleAdapter();
        adapter.setList(itemList);
        recyclerView.setAdapter(adapter);
    }

    private String getRandomText() {
        return Math.abs(random.nextInt()) % 2 == 0 ? "Test item " : "Random item ";
    }

    private void onMenuItemSelected(int menuId) {
        switch (menuId) {
            case R.id.menu_add:
                itemList.add(getRandomText() + itemList.size());
                adapter.setList(itemList);
                adapter.notifyItemInserted(itemList.size() - 1);
                recyclerView.scrollToPosition(itemList.size() - 1);
                break;
            case R.id.menu_close:
                finish();
                break;
            case R.id.menu_edit:
                Toast.makeText(this, "menu_edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_delete:
                actionBar.showConfirmStatus(menuId);
                adapter.setSelect(true);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private boolean onConfirmAction(int actionId) {
        switch (actionId) {
            case R.id.menu_delete:
                delete(adapter.getSelectedData());
                adapter.setSelect(false);
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    private boolean onCancelAction(int actionId) {
        switch (actionId) {
            case R.id.menu_delete:
                adapter.setSelect(false);
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    private void delete(List<String> selectedData) {
        if (selectedData != null) {
            for (String data : selectedData) {
                itemList.remove(data);
            }
            adapter.setList(itemList);
            adapter.notifyDataSetChanged();
        }
    }

    private PopupMenu getSortPopup(View anchor) {
        if (popSort == null) {
            popSort = new PopupMenu(this, anchor);
            popSort.getMenuInflater().inflate(R.menu.menu_sort, popSort.getMenu());
            popSort.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_sort_asc:
                            Toast.makeText(SampleActivity.this, "menu_sort_asc", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.menu_sort_desc:
                            Toast.makeText(SampleActivity.this, "menu_sort_desc", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                }
            });
        }
        return popSort;
    }

    @Override
    public void onBackPressed() {
        // check if it needs to cancel confirm status
        if (actionBar != null && actionBar.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @OnClick({R.id.btn_menu, R.id.btn_visible})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_menu:
                actionBar.updateMenuText(R.id.menu_close, "Quit");
                break;
            case R.id.btn_visible:
                actionBar.updateMenuItemVisible(R.id.menu_edit, false);
                break;
        }
    }
}
