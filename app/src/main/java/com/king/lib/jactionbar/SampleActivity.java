package com.king.lib.jactionbar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.king.app.jactionbar.OnBackListener;
import com.king.app.jactionbar.OnCancelListener;
import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.jactionbar.OnMenuItemListener;
import com.king.app.jactionbar.OnSearchListener;
import com.king.app.jactionbar.OnSelectAllListener;
import com.king.app.jactionbar.PopupMenuProvider;
import com.king.lib.jactionbar.databinding.ActivitySampleBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleActivity extends AppCompatActivity {

    private SampleAdapter adapter;

    private Random random = new Random();

    private List<String> itemList;

    private PopupMenu popSort;
    
    private ActivitySampleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setStatusBarColor(getResources().getColor(R.color.status_bar_bg));

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sample);

        initActionbar();
        initList();

        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.actionBar.updateMenuText(R.id.menu_close, "Quit");
//                binding.actionBar.setMenu(R.menu.menu_sample_less);
                binding.actionBar.updateMenuItemVisible(R.id.menu_add, true);
            }
        });
        binding.btnVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.actionBar.updateMenuItemVisible(R.id.menu_edit, false);
//                binding.actionBar.removeMenu();
                binding.actionBar.updateMenuItemVisible(R.id.menu_add, false);
            }
        });
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
        binding.actionBar.setOnBackListener(new OnBackListener() {
            @Override
            public void onBack() {
                finish();
            }
        });
        // search event, listen to the change of input words
        binding.actionBar.setOnSearchListener(new OnSearchListener() {
            @Override
            public void onSearchWordsChanged(String words) {
                adapter.filter(words);
            }
        });
        // icon or menu item listener
        binding.actionBar.setOnMenuItemListener(new OnMenuItemListener() {
            @Override
            public void onMenuItemSelected(int menuId) {
                SampleActivity.this.onMenuItemSelected(menuId);
            }
        });
        // register popup menu for sort icon
        binding.actionBar.registerPopupMenu(R.id.menu_sort);
        binding.actionBar.setPopupMenuProvider(new PopupMenuProvider() {
            @Override
            public PopupMenu getPopupMenu(int iconMenuId, View anchorView) {
                if (iconMenuId == R.id.menu_sort) {
                    return getSortPopup(anchorView);
                }
                return null;
            }
        });
        // confirm yes/no event
        binding.actionBar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean onConfirm(int actionId) {
                return onConfirmAction(actionId);
            }
        });
        binding.actionBar.setOnCancelListener(new OnCancelListener() {
            @Override
            public boolean onCancel(int actionId) {
                return onCancelAction(actionId);
            }
        });
        binding.actionBar.setOnSelectAllListener(new OnSelectAllListener() {
            @Override
            public boolean onSelectAll(boolean select) {
                adapter.selectAll(select);
                return true;
            }
        });
        binding.actionBar.setTitle("Avengers\nInfinity war");
    }

    private void initList() {

        itemList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            itemList.add(getRandomText() + i);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        adapter = new SampleAdapter();
        adapter.setList(itemList);
        binding.recyclerView.setAdapter(adapter);
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
                binding.recyclerView.scrollToPosition(itemList.size() - 1);
                break;
            case R.id.menu_close:
                finish();
                break;
            case R.id.menu_edit:
                binding.actionBar.showConfirmStatus(menuId, true, "Give up");
                break;
            case R.id.menu_delete:
                binding.actionBar.showConfirmStatus(menuId);
                binding.actionBar.showSelectAll(true);
                adapter.setSelect(true);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private boolean onConfirmAction(int actionId) {
        switch (actionId) {
            case R.id.menu_delete:
                delete(adapter.getSelectedData());
                binding.actionBar.showSelectAll(false);
                binding.actionBar.checkSelectAll(false);
                adapter.setSelect(false);
                adapter.selectAll(false);
                break;
            case R.id.menu_edit:
                Toast.makeText(this, "Give up", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private boolean onCancelAction(int actionId) {
        switch (actionId) {
            case R.id.menu_delete:
                binding.actionBar.showSelectAll(false);
                binding.actionBar.checkSelectAll(false);
                adapter.setSelect(false);
                adapter.selectAll(false);
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
        if (binding.actionBar != null && binding.actionBar.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
