package jp.yuta.kohashi.esc.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import jp.yuta.kohashi.esc.Const;
import jp.yuta.kohashi.esc.R;
import jp.yuta.kohashi.esc.model.PrefItem;
import jp.yuta.kohashi.esc.model.enums.PrefViewType;
import jp.yuta.kohashi.esc.network.HttpConnector;
import jp.yuta.kohashi.esc.network.api.EscApiManager;
import jp.yuta.kohashi.esc.ui.activity.AboutActivity;
import jp.yuta.kohashi.esc.ui.activity.AttendanceDivideActivity;
import jp.yuta.kohashi.esc.ui.activity.AttendanceRateChangeColorActivity;
import jp.yuta.kohashi.esc.ui.activity.LicenceActivity;
import jp.yuta.kohashi.esc.ui.activity.LoginCheckActivity;
import jp.yuta.kohashi.esc.ui.activity.TimeTableChangeActivity;
import jp.yuta.kohashi.esc.ui.activity.TimeTableEnableDisableActivity;
import jp.yuta.kohashi.esc.ui.adapter.PrefRecyclerAdapter;
import jp.yuta.kohashi.esc.ui.fragment.base.BasePrefBaseRecyclerViewFragment;
import jp.yuta.kohashi.esc.ui.service.EccNewsManageService;
import jp.yuta.kohashi.esc.util.NotifyUtil;
import jp.yuta.kohashi.esc.util.Util;
import jp.yuta.kohashi.esc.util.preference.PrefUtil;

/**
 * 設定画面
 * PrefrenceFragmentを継承しない
 */
public class PreferenceMainFragment extends BasePrefBaseRecyclerViewFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 表示する項目を作成
     */
    @Override
    public void createItems() {
        addItem(new PrefItem(getResources().getString(R.string.pref_group_title_time_table), PrefViewType.ITEM_GROUP_TITLE));
        addItem(new PrefItem(getResources().getString(R.string.pref_update_time_table), R.drawable.ic_refresh, PrefViewType.ITEM));
        addItem(new PrefItem(getResources().getString(R.string.pref_change_time_table), R.drawable.ic_create, PrefViewType.ITEM_RIGHT_ARROW));
        addItem(new PrefItem(getResources().getString(R.string.pref_enable_time_table), R.drawable.ic_playlist_add_check, PrefViewType.ITEM_RIGHT_ARROW));
        addItem(new PrefItem(getResources().getString(R.string.pref_group_title_time_attendance), PrefViewType.ITEM_GROUP_TITLE));
        addItem(new PrefItem(getResources().getString(R.string.pref_attendance_divide), R.drawable.ic_view_compact, PrefViewType.ITEM_RIGHT_ARROW));
        addItem(new PrefItem(getResources().getString(R.string.pref_attendance_color), R.drawable.ic_brush, PrefViewType.ITEM_RIGHT_ARROW));
        addItem(new PrefItem(getResources().getString(R.string.pref_group_title_news), PrefViewType.ITEM_GROUP_TITLE));
        addItem(new PrefItem(getResources().getString(R.string.pref_notify_news), R.drawable.ic_notifications, PrefViewType.ITEM_SWITCH, PrefUtil.isNotifyNews()));
        addItem(new PrefItem(getResources().getString(R.string.pref_group_title_schedule), PrefViewType.ITEM_GROUP_TITLE));
        addItem(new PrefItem(getResources().getString(R.string.pref_update_schedule), R.drawable.ic_refresh, PrefViewType.ITEM));
        addItem(new PrefItem(getResources().getString(R.string.pref_group_title_time_other), PrefViewType.ITEM_GROUP_TITLE));
        addItem(new PrefItem(getResources().getString(R.string.pref_lisence), R.drawable.ic_business, PrefViewType.ITEM_RIGHT_ARROW));
        addItem(new PrefItem(getResources().getString(R.string.pref_about), R.drawable.ic_about, PrefViewType.ITEM_RIGHT_ARROW));
        addItem(new PrefItem(getResources().getString(R.string.pref_app_version), Const.APP_VERSION, R.drawable.ic_android, PrefViewType.ITEM_RIGHT_TXT));
        addItem(new PrefItem(PrefViewType.EMPTY));
        addItem(new PrefItem(getResources().getString(R.string.pref_logout), PrefViewType.ITEM_CENTER_TXT));
        addItem(new PrefItem(PrefViewType.EMPTY));
    }

    @Override
    public void initView(View view) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        createAdapter(new PrefRecyclerAdapter(getItems(), getContext()) {
            @Override
            protected void onItemClicked(@NonNull PrefItem model) {
                super.onItemClicked(model);
                String name = model.getItemName();
                if (name.equals(getResources().getString(R.string.pref_update_time_table))) {
                    updateTimeTable();
                } else if (name.equals(getResources().getString(R.string.pref_change_time_table))) {
                    changeTimeTable();
                } else if(name.equals(getResources().getString(R.string.pref_enable_time_table))){
                    setDisableEnableSettings();
                } else if (name.equals(getResources().getString(R.string.pref_attendance_divide))) {
                    divideData();
                } else if (name.equals(getResources().getString(R.string.pref_attendance_color))) {
                    changeColorAttendance();
                } else if(name.equals(getResources().getString(R.string.pref_update_schedule))){
                    updateSchedule();
                } else if (name.equals(getResources().getString(R.string.pref_lisence))) {
                    showLicence();
                } else if (name.equals(getResources().getString(R.string.pref_about))) {
                    showAbout();
                } else if (name.equals(getResources().getString(R.string.pref_logout))) {
                    logout();
                }
            }

            @Override
            protected void onItemCheckedChange(@NonNull boolean bool, @NonNull PrefItem model) {
                super.onItemCheckedChange(bool, model);
                if (model.getItemName().equals(getResources().getString(R.string.pref_notify_news))) {
                    PrefUtil.saveNotifyNews(bool);
                    if (bool) {
                        if (!Util.isStartService()) {
                            // サービス開始
                            new EccNewsManageService().startResident(getContext());
                        } else {
                            Log.d(PreferenceMainFragment.class.getSimpleName(), "すでにサービス起動済み");
                        }
                    } else {
                        EccNewsManageService.stopResidentIfActive(getContext()); //サービス停止
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    protected void swap() {
    }

    @Override
    protected void getSavedItems() {
    }

    /**
     * 時間割を更新
     */
    private void updateTimeTable() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .content(R.string.dialog_time_table_update_comment)
                .positiveText(R.string.dialog_positive_ok)
                .negativeText(R.string.dialog_negative_cancel)
                .title(R.string.dialog_title_update_check)
                .positiveColor(Util.getColor(R.color.diag_text_color_cancel))
                .negativeColor(Util.getColor(R.color.colorPrimary))
                .onPositive(((dialog, which) -> {
                    dialog.dismiss();
                    if (!Util.netWorkCheck()) {
                        NotifyUtil.failureNetworkConnection();
                    } else {
                        NotifyUtil.showUpdatingDiag(getActivity());
                        String userId = PrefUtil.getId();
                        String password = PrefUtil.getPss();
                        HttpConnector.request(HttpConnector.Type.TIME_TABLE, userId, password, (bool -> {
                            NotifyUtil.dismiss();
                            if (bool) {
                                NotifyUtil.successUpdate();
                            } else {
                                NotifyUtil.failureUpdate();
                            }
                        }));
                    }
                }))
                .onNegative(((dialog, which) -> dialog.dismiss()));

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    /**
     * 時間割を変更する
     */
    private void changeTimeTable() {
        startActivity(new Intent(getActivity(), TimeTableChangeActivity.class));
    }

    /**
     * 有効・向こう設定
     */
    private void setDisableEnableSettings(){
        startActivity(new Intent(getActivity(), TimeTableEnableDisableActivity.class));
    }

    /**
     * 前期後期
     */
    private void divideData() {
        startActivity(new Intent(getActivity(), AttendanceDivideActivity.class));
    }

    /**
     * 出席照会を色分け
     */
    private void changeColorAttendance() {
        startActivity(new Intent(getActivity(), AttendanceRateChangeColorActivity.class));
    }

    /**
     * スケジュールを更新
     */
    private void updateSchedule(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .content(R.string.dialog_schedule_update_comment)
                .positiveText(R.string.dialog_positive_ok)
                .negativeText(R.string.dialog_negative_cancel)
                .title(R.string.pref_update_schedule)
                .positiveColor(Util.getColor(R.color.diag_text_color_cancel))
                .negativeColor(Util.getColor(R.color.colorPrimary))
                .onPositive(((dialog, which) -> {
                    dialog.dismiss();
                    if (!Util.netWorkCheck()) {
                        NotifyUtil.failureNetworkConnection();
                    } else {
                        NotifyUtil.showUpdatingDiag(getActivity());
                        String userId = PrefUtil.getId();
                        String password = PrefUtil.getPss();
                        HttpConnector.request(HttpConnector.Type.SCHEDULE, userId, password, (bool -> {
                            NotifyUtil.dismiss();
                            if (bool) {
                                NotifyUtil.successUpdate();
                            } else {
                                NotifyUtil.failureUpdate();
                            }
                        }));
                    }
                }))
                .onNegative(((dialog, which) -> dialog.dismiss()));

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    /**
     * 著作権情報
     */
    private void showLicence() {
        startActivity(new Intent(getActivity(), LicenceActivity.class));
    }

    /**
     * このアプリについて
     */
    private void showAbout() {
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    /**
     * ログアウト
     */
    private void logout() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .content(R.string.dialog_comment_comment)
                .positiveText(R.string.dialog_positive_ok)
                .negativeText(R.string.dialog_negative_cancel)
                .positiveColor(Util.getColor(R.color.diag_text_color_cancel))
                .negativeColor(Util.getColor(R.color.colorPrimary))
                .onPositive(((dialog, which) -> {
                    NotifyUtil.showLogoutingDiag(getActivity());
                    Handler mHandler = new Handler();
                    Runnable runnable = () -> {
                        PrefUtil.deleteAll();
                        try{
                            PrefUtil.deleteSharedPreferencesFiles();
                        }catch(Throwable t){

                        }
                        /**
                         * サービスを終了
                         */
                        EccNewsManageService.stopResidentIfActive(getContext());
                        EscApiManager.resetToken();
                        NotifyUtil.dismiss();
                        Intent intent = new Intent(getContext().getApplicationContext(), LoginCheckActivity.class);
                        startActivity(intent);
                        ActivityCompat.finishAffinity(getActivity());
                    };
                    mHandler.postDelayed(runnable, 2000);
                }))
                .onNegative((dialog, which) -> dialog.dismiss());

        MaterialDialog dialog = builder.build();
        dialog.show();

    }
}
