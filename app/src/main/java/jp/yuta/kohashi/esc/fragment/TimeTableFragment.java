package jp.yuta.kohashi.esc.fragment;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import jp.yuta.kohashi.esc.R;
import jp.yuta.kohashi.esc.adapter.RecyclerViewAdapter;
import jp.yuta.kohashi.esc.adapter.TimeTableListAdapter;
import jp.yuta.kohashi.esc.object.TimeBlock;
import jp.yuta.kohashi.esc.preference.LoadManager;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeTableFragment extends Fragment {

    static final String prefName ="sample";

    //月曜日から金曜日の曜日ごとのリスト
    List<TimeBlock> MondayList;
    List<TimeBlock> TuesdayList;
    List<TimeBlock> WednesdayList;
    List<TimeBlock> ThursdayList;
    List<TimeBlock> FridayList;

    LinearLayout monLayout;
    LinearLayout tueLayout;
    LinearLayout wedLayout;
    LinearLayout thurLayout;
    LinearLayout friLayout;

    RecyclerView monList;
    RecyclerView tueList;
    RecyclerView wedList;
    RecyclerView thurList;
    RecyclerView friList;
    RecyclerView.LayoutManager layoutManager;

    LoadManager loadManager;

    TextView textView;
    TextView latestUpText;
 //   LayoutInflater inflater;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    public TimeTableFragment newInstance() {
        TimeTableFragment frag = new TimeTableFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_table, container, false);

        //getActivity().findViewById(R.id.toolbar).scrollf
//
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        if(actionBar != null){
//            actionBar.hide();
//
//        }
        MondayList = new ArrayList<>();
        TuesdayList = new ArrayList<>();
        WednesdayList = new ArrayList<>();
        ThursdayList = new ArrayList<>();
        FridayList = new ArrayList<>();


//        monLayout = (LinearLayout)v.findViewById(R.id.mon_col);
//        tueLayout =(LinearLayout)v.findViewById(R.id.tue_col);
//        wedLayout = (LinearLayout)v.findViewById(R.id.wed_col);
//        thurLayout = (LinearLayout)v.findViewById(R.id.thur_col);
//        friLayout = (LinearLayout)v.findViewById(R.id.fri_col);

        loadManager = new LoadManager();

        MondayList = loadManager.loadManagerWithPreferenceForTimeBlock(getActivity(),prefName,"monList");
        TuesdayList = loadManager.loadManagerWithPreferenceForTimeBlock(getActivity(),prefName,"tueList");
        WednesdayList = loadManager.loadManagerWithPreferenceForTimeBlock(getActivity(),prefName,"wedList");
        ThursdayList = loadManager.loadManagerWithPreferenceForTimeBlock(getActivity(),prefName,"thurList");
        FridayList = loadManager.loadManagerWithPreferenceForTimeBlock(getActivity(),prefName,"friList");

//        setBlockToTimeTabe(MondayList,monLayout,inflater);
//        setBlockToTimeTabe(TuesdayList,tueLayout,inflater);
//        setBlockToTimeTabe(WednesdayList,wedLayout,inflater);
//        setBlockToTimeTabe(ThursdayList,thurLayout,inflater);
//        setBlockToTimeTabe(FridayList,friLayout,inflater);
//        monLayout.removeAllViews();
//        for(int i = 0;i < MondayList.size();i++){
//
//            TimeBlock timeBlock = MondayList.get(i);
//            String subStr =timeBlock.getSubject();
//            String roomStr = timeBlock.getClassRoom();
//            CardView cardView = (CardView)inflater.inflate(R.layout.time_table_block, monLayout, false);
//            cardView.setMinimumHeight(300);
//            TextView mSubjectTextView = (TextView)cardView.findViewById(R.id.text_subject);
//            TextView mClassRoomTextView = (TextView)cardView.findViewById(R.id.text_classRoom);
//            mSubjectTextView.setText(subStr);
//            mClassRoomTextView.setText(roomStr);
//
//            monLayout.addView(cardView);
//        }
//
//        tueLayout.removeAllViews();
//        for(int i = 0;i < TuesdayList.size();i++){
//
//            TimeBlock timeBlock = TuesdayList.get(i);
//            String subStr =timeBlock.getSubject();
//            String roomStr = timeBlock.getClassRoom();
//            CardView cardView = (CardView)inflater.inflate(R.layout.time_table_block, tueLayout, false);
//            cardView.setMinimumHeight(300);
//            TextView mSubjectTextView = (TextView)cardView.findViewById(R.id.text_subject);
//            TextView mClassRoomTextView = (TextView)cardView.findViewById(R.id.text_classRoom);
//            mSubjectTextView.setText(subStr);
//            mClassRoomTextView.setText(roomStr);
//
//            tueLayout.addView(cardView);
//        }
//
//        wedLayout.removeAllViews();
//        for(int i = 0;i < WednesdayList.size();i++){
//
//            TimeBlock timeBlock = WednesdayList.get(i);
//            String subStr =timeBlock.getSubject();
//            String roomStr = timeBlock.getClassRoom();
//            CardView cardView = (CardView)inflater.inflate(R.layout.time_table_block, wedLayout, false);
//            cardView.setMinimumHeight(300);
//            TextView mSubjectTextView = (TextView)cardView.findViewById(R.id.text_subject);
//            TextView mClassRoomTextView = (TextView)cardView.findViewById(R.id.text_classRoom);
//            mSubjectTextView.setText(subStr);
//            mClassRoomTextView.setText(roomStr);
//
//            wedLayout.addView(cardView);
//        }
//
//        thurLayout.removeAllViews();
//        for(int i = 0;i < TuesdayList.size();i++){
//
//            TimeBlock timeBlock = ThursdayList.get(i);
//            String subStr =timeBlock.getSubject();
//            String roomStr = timeBlock.getClassRoom();
//            CardView cardView = (CardView)inflater.inflate(R.layout.time_table_block, thurLayout, false);
//            cardView.setMinimumHeight(300);
//            TextView mSubjectTextView = (TextView)cardView.findViewById(R.id.text_subject);
//            TextView mClassRoomTextView = (TextView)cardView.findViewById(R.id.text_classRoom);
//            mSubjectTextView.setText(subStr);
//            mClassRoomTextView.setText(roomStr);
//
//            thurLayout.addView(cardView);
//        }
//
//        friLayout.removeAllViews();
//        for(int i = 0;i < FridayList.size();i++){
//
//            TimeBlock timeBlock = FridayList.get(i);
//            String subStr =timeBlock.getSubject();
//            String roomStr = timeBlock.getClassRoom();
//            CardView cardView = (CardView)inflater.inflate(R.layout.time_table_block, friLayout, false);
//            cardView.setMinimumHeight(300);
//            TextView mSubjectTextView = (TextView)cardView.findViewById(R.id.text_subject);
//            TextView mClassRoomTextView = (TextView)cardView.findViewById(R.id.text_classRoom);
//            mSubjectTextView.setText(subStr);
//            mClassRoomTextView.setText(roomStr);
//
//            friLayout.addView(cardView);
//        }

        //タイトルの設定
        textView = (TextView)getActivity().findViewById(R.id.title_name_text);
        textView.setText("時間割");

        //リスト
        monList = (RecyclerView)v.findViewById(R.id.mon_col);
        tueList = (RecyclerView)v.findViewById(R.id.tue_col);
        wedList = (RecyclerView)v.findViewById(R.id.wed_col);
        thurList = (RecyclerView)v.findViewById(R.id.thur_col);
        friList = (RecyclerView)v.findViewById(R.id.fri_col);

        TimeTableListAdapter monAdapter = new TimeTableListAdapter(MondayList,getActivity());
        monList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        monList.setLayoutManager(layoutManager);
        monList.setAdapter(monAdapter);

        TimeTableListAdapter tueAdapter = new TimeTableListAdapter(TuesdayList,getActivity());
        tueList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        tueList.setLayoutManager(layoutManager);
        tueList.setAdapter(tueAdapter);

        TimeTableListAdapter wedAdapter = new TimeTableListAdapter(WednesdayList,getActivity());
        wedList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        wedList.setLayoutManager(layoutManager);
        wedList.setAdapter(wedAdapter);

        TimeTableListAdapter thurAdapter = new TimeTableListAdapter(ThursdayList,getActivity());
        thurList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        thurList.setLayoutManager(layoutManager);
        thurList.setAdapter(thurAdapter);

        TimeTableListAdapter friAdapter = new TimeTableListAdapter(FridayList,getActivity());
        friList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        friList.setLayoutManager(layoutManager);
        friList.setAdapter(friAdapter);

        return v;
    }

    //時間割の各ブロックのレイアウトを設定するクラス
    /**
     *
     * @param list
     * @param containerLayout
     * @param inflater
     */
    CardView cardView;
    private void setBlockToTimeTabe(List<TimeBlock> list, LinearLayout containerLayout,LayoutInflater inflater){
        containerLayout.removeAllViews();
        for(int i = 0;i < list.size();i++){

            TimeBlock timeBlock = list.get(i);
            String subStr =timeBlock.getSubject();
            String roomStr = timeBlock.getClassRoom();
            cardView = (CardView)inflater.inflate(R.layout.time_table_block, containerLayout, false);
            cardView.setMinimumHeight(300);
            TextView mSubjectTextView = (TextView)cardView.findViewById(R.id.text_subject);
            TextView mClassRoomTextView = (TextView)cardView.findViewById(R.id.text_classRoom);
            mSubjectTextView.setText(subStr);
            mClassRoomTextView.setText(roomStr);

            containerLayout.addView(cardView);

            //各ブロックのClickイベント
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewParent parentLayout =  cardView.getParent();
//                    parentLayout

                    TextView mSubjectTextView = (TextView)cardView.findViewById(R.id.text_subject);
                    String subjectName = mSubjectTextView.getText().toString();

                    Toast.makeText(getActivity(),subjectName,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
