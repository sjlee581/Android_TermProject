package com.example.mygrid;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.TabActivity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Ex11_Calendar extends TabActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener{

    ArrayList<String> mItems;
    ArrayAdapter<String> adapter;
    TextView textYear;
    TextView textMon;
    TextView textDay;
    Button bt_account;
    LocationManager locationManager = null;
    LocationListener locationListener = null;
    TextView coronaText;
    TextView weatherText;
    String weatherKey = "t3DeY%2BdP1a2uVd8P4Ra9ITVulHZo7LvwgM%2F8yJefOxWx0XUykDUA0B0HjD5KLhvaNONaYyvdAekmQSYFQ7gBvA%3D%3D";
    String weatherData;
    String coronaKey =  "t3DeY%2BdP1a2uVd8P4Ra9ITVulHZo7LvwgM%2F8yJefOxWx0XUykDUA0B0HjD5KLhvaNONaYyvdAekmQSYFQ7gBvA%3D%3D";
    String coronaData;

    public static int TO_GRID = 0;
    public static int TO_GPS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        textYear=(TextView) this.findViewById(R.id.edit1);
        textMon=(TextView) this.findViewById(R.id.edit2);

        TabHost tabHost = getTabHost();
        TabSpec tabSpecTab1 = tabHost.newTabSpec("TAB1").setIndicator("일정");
        tabSpecTab1.setContent(R.id.tab1);
        tabHost.addTab(tabSpecTab1);
        TabSpec tabSpecTab2 = tabHost.newTabSpec("TAB2").setIndicator("날씨");
        tabSpecTab2.setContent(R.id.tab2);
        tabHost.addTab(tabSpecTab2);
        TabSpec tabSpecTab3 = tabHost.newTabSpec("TAB3").setIndicator("메모");
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

        final Date date = new Date();
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

        // TAB 2
        settingGPS();
        Location userLocation = getMyLocation();
        double latitude = 35.231977;
        double longitude = 129.015498;
        if( userLocation != null ) {
            // TODO 위치를 처음 얻어왔을 때 하고 싶은 것
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
        }

        final LatXLngY tmp = convertGRID_GPS(TO_GRID, latitude, longitude);
        Log.e(">>", "x = " + tmp.x + ", y = " + tmp.y);
        weatherText= (TextView)findViewById(R.id.weatherresult);
        final double finalLongitude = longitude;
        final double finalLatitude = latitude;
        final double finalLongitude1 = longitude;
        final double finalLatitude1 = latitude;
        weatherText.append(longitude + " " + latitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer buffer= new StringBuffer();
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat simpleDate2 = new SimpleDateFormat("HHmm");
                String gethour = simpleDate2.format(mDate);
                String getTime = simpleDate.format(mDate);
                String queryUrl="http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=t3DeY%2BdP1a2uVd8P4Ra9ITVulHZo7LvwgM%2F8yJefOxWx0XUykDUA0B0HjD5KLhvaNONaYyvdAekmQSYFQ7gBvA%3D%3D&pageNo=1&numOfRows=10&dataType=XML&base_date="+getTime+"&base_time="+gethour+"&nx="+Integer.parseInt(String.valueOf(Math.round(tmp.x))) + "&ny=" + Integer.parseInt(String.valueOf(Math.round(tmp.y))) + "&";
                weatherText.append(queryUrl + "  ");
                try{
                    //weatherText.append("WEATHER PART  ");
                    URL url = new URL(queryUrl);
                    InputStream is= url.openStream();
                    XmlPullParserFactory factory= XmlPullParserFactory.newInstance();//xml파싱을 위한
                    XmlPullParser xpp= factory.newPullParser();
                    xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기
                    String tag;
                    xpp.next();
                    int eventType= xpp.getEventType();
                    boolean isItemTag = false;
                    boolean dateonce = true;
                    boolean timeonece = true;
                    while( eventType != XmlPullParser.END_DOCUMENT){
                        //weatherText.append("WEATHER PART2  ");
                        switch( eventType ){
                            case XmlPullParser.START_DOCUMENT:
                                buffer.append("파싱 시작...\n\n");
                                break;
                            case XmlPullParser.START_TAG:
                                tag= xpp.getName();//테그 이름 얻어오기
                                if(tag.equals("item"))  ;// 첫번째 검색결과
                                else if(tag.equals("baseDate") && dateonce){
                                    dateonce = false;
                                    buffer.append("발표일자 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());//title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n"); //줄바꿈 문자 추가
                                }
                                else if(tag.equals("baseTime") && timeonece){
                                    timeonece = false;
                                    buffer.append("발표시간 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");//줄바꿈 문자 추가
                                }
                                else if(tag.equals("category")){
                                    xpp.next();
                                    if(xpp.getText().equals("POP")) {
                                        //강수확률
                                        buffer.append(" 강수확률 ");
                                        xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next();
                                        buffer.append(xpp.getText());
                                        buffer.append("\n");
                                    }
                                    else if(xpp.getText().equals("REH")){
                                        //습도
                                        buffer.append(" 습도 ");
                                        xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next();
                                        buffer.append(xpp.getText());
                                        buffer.append("\n");
                                    }
                                    else if(xpp.getText().equals("T3H")){
                                        //기온
                                        buffer.append(" 기온 ");
                                        xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next();
                                        buffer.append(xpp.getText());
                                        buffer.append("\n");
                                    }
                                    else if(xpp.getText().equals("WSD")){
                                        //풍속
                                        buffer.append(" 풍속 ");
                                        xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next();
                                        buffer.append(xpp.getText());
                                        buffer.append("\n");
                                    }
                                    else if(xpp.getText().equals("SKY")){
                                        //하늘
                                        buffer.append(" 하늘 ");
                                        xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next(); xpp.next();
                                        buffer.append(xpp.getText());
                                        buffer.append("\n");
                                    }
                                }
                                break;

                            case XmlPullParser.TEXT:
                                break;

                            case XmlPullParser.END_TAG:
                                tag= xpp.getName(); //테그 이름 얻어오기
                                if(tag.equals("item")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                                break;
                        }

                        eventType= xpp.next();
                    }
                }  catch (Exception e) {
                    e.printStackTrace();
                }
                weatherData = buffer.toString();
                weatherText.append(weatherData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherText.append(weatherData);

                    }
                });
            }
        }).start();

        coronaText= (TextView)findViewById(R.id.coronaresult);
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer buffer= new StringBuffer();
                String queryUrl="http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey=t3DeY%2BdP1a2uVd8P4Ra9ITVulHZo7LvwgM%2F8yJefOxWx0XUykDUA0B0HjD5KLhvaNONaYyvdAekmQSYFQ7gBvA%3D%3D&pageNo=1&numOfRows=10&startCreateDt=20200705&endCreateDt=20200706&";
                try{
                    coronaText.append("CoRONA PART 1 ");
                    URL url = new URL(queryUrl);
                    InputStream is= url.openStream();
                    XmlPullParserFactory factory= XmlPullParserFactory.newInstance();//xml파싱을 위한
                    XmlPullParser xpp= factory.newPullParser();
                    xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기
                    String tag;
                    xpp.next();
                    int eventType= xpp.getEventType();
                    while( eventType != XmlPullParser.END_DOCUMENT ){
                        switch( eventType ){
                            case XmlPullParser.START_DOCUMENT:
                                buffer.append("파싱 시작...\n\n");
                                break;

                            case XmlPullParser.START_TAG:
                                tag= xpp.getName();//테그 이름 얻어오기

                                if(tag.equals("item")) ;// 첫번째 검색결과
                                else if(tag.equals("createDt")){
                                    buffer.append("생성일 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());//title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n"); //줄바꿈 문자 추가
                                }
                                else if(tag.equals("deathCnt")){
                                    buffer.append("사망자수 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");//줄바꿈 문자 추가
                                }
                                else if(tag.equals("decideCnt")){
                                    buffer.append("확진자수 :");
                                    xpp.next();
                                    buffer.append(xpp.getText());//description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");//줄바꿈 문자 추가
                                }
                                break;

                            case XmlPullParser.TEXT:
                                break;

                            case XmlPullParser.END_TAG:
                                tag= xpp.getName(); //테그 이름 얻어오기

                                if(tag.equals("item")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                                break;
                        }

                        eventType= xpp.next();
                    }

                } catch (Exception e) {
                    coronaText.append(e.toString());
                    e.printStackTrace();
                }
                coronaData = buffer.toString();
                coronaText.append(coronaData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        coronaText.append(coronaData);
                    }
                });
            }
        }).start();

    }

    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 사용자 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1004);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
                Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
            }
        }
        return currentLocation;
    }


    private void settingGPS() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // TODO 위도, 경도로 하고 싶은 것
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }
    int rqCode = 1004;
    boolean canReadLocation = false;
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == rqCode) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// success!
                Location userLocation = getMyLocation();
                if( userLocation != null ) {
// 다음 데이터 //
// todo 사용자의 현재 위치 구하기
                    double latitude = userLocation.getLatitude();
                    double longitude = userLocation.getLongitude();
                }
                canReadLocation = true;
            } else {
// Permission was denied or request was cancelled
                canReadLocation = false;
            }
        }
    }

    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y )
    {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //


        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GRID) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        }
        else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }
        return rs;
    }



    class LatXLngY
    {
        public double lat;
        public double lng;

        public double x;
        public double y;

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