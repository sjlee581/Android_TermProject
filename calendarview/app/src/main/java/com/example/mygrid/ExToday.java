package com.example.mygrid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ExToday extends Activity implements AdapterView.OnItemClickListener,
        View.OnClickListener {
    MyDBHelper mDBHelper;
    String today;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    ListView list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extoday);

        Intent intent = getIntent();
        today = intent.getStringExtra("Param1");
        TextView text = (TextView) findViewById(R.id.texttoday);
        text.setText(today);
        mDBHelper = new MyDBHelper(this, "Today.db", null, 1);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        cursor = db.rawQuery(
                "SELECT * FROM today WHERE date='" + today + "'", null);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor, new String[]{
                "title", "time"}, new int[]{android.R.id.text1,
                android.R.id.text2});
        ListView list = (ListView) findViewById(R.id.list1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        mDBHelper.close();

        Button btn = (Button) findViewById(R.id.btnadd);
        btn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, Detail.class);
        intent.putExtra("ParamDate", today);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, Detail.class);
        cursor.moveToPosition(position);
        intent.putExtra("ParamID", cursor.getInt(0));
        startActivityForResult(intent, 0);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 0:
            case 1:
                if(resultCode == RESULT_OK){
                    SQLiteDatabase db = mDBHelper.getWritableDatabase();
                    cursor = db.rawQuery("SELECT * FROM today WHERE date = '"
                                        +today+"'",null);
                    adapter.changeCursor(cursor);
                    mDBHelper.close();
                }
                break;
        }
    }
}
