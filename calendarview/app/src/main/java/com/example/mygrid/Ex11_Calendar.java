package com.example.mygrid;

import java.util.Calendar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import org.w3c.dom.Text;
import android.app.TabActivity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Ex11_Calendar extends TabActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener{
    ArrayList<String> mItems;
    ArrayAdapter<String> adapter;
    TextView textYear;
    TextView textMon;
    TextView textDay;
    Button bt_account;
    Button bt_dest;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
        textYear=(TextView) this.findViewById(R.id.edit1);
        textMon=(TextView) this.findViewById(R.id.edit2);

        TabHost tabHost = getTabHost();
        TabSpec tabSpecTab1 = tabHost.newTabSpec("TAB1").setIndicator("일정");
        tabSpecTab1.setContent(R.id.tab1);
        tabHost.addTab(tabSpecTab1);
        TabSpec tabSpecTab2 = tabHost.newTabSpec("TAB2").setIndicator("날씨");
        tabSpecTab2.setContent(R.id.tab2);
        tabHost.addTab(tabSpecTab2);
        TabSpec tabSpecTab3 = tabHost.newTabSpec("TAB3").setIndicator("지도");
        tabSpecTab3.setContent(R.id.tab3);
        tabHost.addTab(tabSpecTab3);
        tabHost.setCurrentTab(0);

        mItems=new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,mItems){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                TextView textView = (TextView) super.getView(position,convertView, parent);
                if(position%7==0){
                    textView.setTextColor(Color.parseColor("#FF0000"));
                }else if(position%7 == 6){
                    textView.setTextColor(Color.parseColor("#0000FF"));
                }else{
                    textView.setTextColor(Color.parseColor("#000000"));
                }
                return textView;
            }
        };
        GridView gird = (GridView) this.findViewById(R.id.grid1);
        gird.setAdapter(adapter);
        gird.setOnItemClickListener(this);

        Date date = new Date();
        int year = date.getYear()+1900;
        int mon = date.getMonth()+1;
        textYear.setText(year+"");
        textMon.setText(mon+"");

        fillDate(year, mon);
        Button btnmove = (Button) this.findViewById(R.id.bt1);
        Button btnlastmon = (Button)this.findViewById(R.id.bt2);
        Button btnnextmon = (Button)this.findViewById(R.id.bt3);
        btnlastmon.setOnClickListener(this);
        btnnextmon.setOnClickListener(this);
        btnmove.setOnClickListener(this);

        bt_account = (Button) findViewById(R.id.bt_account);
        bt_account.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.example.mygrid.LoginActivity.class);
                //응답을 받고 싶으면 startActivityForResult(intent, 101) 쓰기.
                startActivity(intent);
            }
        });
        bt_dest = (Button) findViewById(R.id.bt_dest);
        bt_dest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), com.example.mygrid.MapsActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if(mItems.get(arg2).equals("")){
            ;
        }
        else{
            Intent intent = new Intent(this, ExToday.class);
            intent.putExtra("Param1", textYear.getText().toString()+"/"
                    +textMon.getText().toString()+"/"+mItems.get(arg2));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View arg0) {
        if(arg0.getId()==R.id.bt2){
            int year = Integer.parseInt(textYear.getText().toString());
            int mon = Integer.parseInt(textMon.getText().toString());
            if(mon==1)
            {
                mon=13;
                --year;
                fillDate(year, mon-1);
                textYear.setText(year+"");
                textMon.setText((mon-1)+"");
            }
            else
            {
                fillDate(year, mon-1);
                textYear.setText(year+"");
                textMon.setText((mon-1)+"");
            }
        }
        else if(arg0.getId()==R.id.bt3){
            int year = Integer.parseInt(textYear.getText().toString());
            int mon = Integer.parseInt(textMon.getText().toString());
            if(mon==12)
            {
                ++year;
                mon=0;
                fillDate(year, mon+1);
                textYear.setText(year+"");
                textMon.setText((mon+1)+"");
            }
            else{
                fillDate(year, mon+1);
                textYear.setText(year+"");
                textMon.setText((mon+1)+"");
            }
        }
        else{
            int year = Integer.parseInt(textYear.getText().toString());
            int mon = Integer.parseInt(textMon.getText().toString());
            fillDate(year, mon);
        }
    }

    private void fillDate(int year, int mon) {

        mItems.clear();

        mItems.add("일");
        mItems.add("월");
        mItems.add("화");
        mItems.add("수");
        mItems.add("목");
        mItems.add("금");
        mItems.add("토");

        Date current = new Date(year - 1900, mon - 1, 1);

        int day = current.getDay();

        for (int i = 0; i < day; i++) {
            mItems.add("");
        }

        current.setDate(32);

        int last = 32 - current.getDate();

        for (int i = 1; i <= last; i++) {
            mItems.add(i + "");
        }
        adapter.notifyDataSetChanged();
    }
}