package jp.yuta.kohashi.esc.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.yuta.kohashi.esc.R;
import jp.yuta.kohashi.esc.model.PrefItemModel;
import jp.yuta.kohashi.esc.ui.adapter.PrefRecyclerAdapter;

/**
 * 設定画面
 * PrefrenceFragmentを継承しない
 */
public class PreferenceFragment extends Fragment {

    RecyclerView mRecyclerView;
    PrefRecyclerAdapter mRecyclerAdapter;
    List<PrefItemModel> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference, container, false);
        createItems();
        initView(view);
        return view;
    }

    /**
     * 表示する項目を作成
     */
    private void createItems(){
        items = new ArrayList<>();
        items.add(new PrefItemModel("時間割", PrefItemModel.ViewType.ITEM_GROUP_TITLE));
        items.add(new PrefItemModel("時間割を更新",R.drawable.ic_error, PrefItemModel.ViewType.ITEM));
        items.add(new PrefItemModel("時間割データを変更",R.drawable.ic_error, PrefItemModel.ViewType.ITEM));
        items.add(new PrefItemModel(PrefItemModel.ViewType.EMPTY));
        items.add(new PrefItemModel("出席照会", PrefItemModel.ViewType.ITEM_GROUP_TITLE));
        items.add(new PrefItemModel("出席照会を色分けする",R.drawable.ic_error, PrefItemModel.ViewType.ITEM));
        items.add(new PrefItemModel("ログを確認する",R.drawable.ic_error, PrefItemModel.ViewType.ITEM));
        items.add(new PrefItemModel(PrefItemModel.ViewType.EMPTY));
        items.add(new PrefItemModel("その他", PrefItemModel.ViewType.ITEM_GROUP_TITLE));
        items.add(new PrefItemModel("著作権情報",R.drawable.ic_error, PrefItemModel.ViewType.ITEM));
        items.add(new PrefItemModel("このアプリについて",R.drawable.ic_error, PrefItemModel.ViewType.ITEM));
        items.add(new PrefItemModel("アプリバージョン",R.drawable.ic_error, PrefItemModel.ViewType.ITEM));
        items.add(new PrefItemModel(PrefItemModel.ViewType.EMPTY));
        items.add(new PrefItemModel("ログアウト", PrefItemModel.ViewType.ITEM_CENTER_TXT));
        items.add(new PrefItemModel(PrefItemModel.ViewType.EMPTY));
    }

    private void initView(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pref_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new PrefRecyclerAdapter(items, getContext()){
            @Override
            protected void onItemClicked(@NonNull PrefItemModel model) {
                super.onItemClicked(model);
            }
        };
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }
}
