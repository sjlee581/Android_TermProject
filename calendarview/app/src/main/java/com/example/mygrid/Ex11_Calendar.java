package com.example.mygrid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class Ex11_Calendar extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener{
    ArrayList<String> mItems;
    ArrayAdapter<String> adapter;
    TextView textYear;
    TextView textMon;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textYear=(TextView) this.findViewById(R.id.edit1);
        textMon=(TextView) this.findViewById(R.id.edit2);

        mItems=new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,mItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                //gridview의 토요일-> 파란색, 일요일-> 빨간색으로 바꾸기
                if(position%7==0){
                    // Set the color red
                    textView.setTextColor(Color.parseColor("#FF0000"));
                } else if(position%7==6){
                    // Set the color blue
                    textView.setTextColor(Color.parseColor("#0000FF"));
                } else{
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

        Button btnmove= (Button) this.findViewById(R.id.bt1);
        btnmove.setOnClickListener(this);
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
        if(arg0.getId()==R.id.bt1){
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
