package com.example.quectel.quectel_gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.quectel.quectel_gps.MainActivity.flag_cold;
import static com.example.quectel.quectel_gps.MainActivity.flag_hot;

public class GPS_hot extends AppCompatActivity {
    private LocationManager locationManager;
    private String locationProvider;
    public final  String TAG="gps hot";
    public  long time_start;
    public  long time_end;
    public  long diff_time;
    public long time_now;
    public  long sum_time;
    public double time_average;
    public  long time_start_forsum;
    public  long time_end_forsum;
    public  long sum_time_foraverage;
    public long time_for_total; // ui total time
    private boolean flag_gps=false;  // has get position
    private boolean flag_send_msg_listener=false; //control statistics
    private boolean flag_time_start=false;  //control time start
    private boolean flag_addlistener= false; // control listener
    private boolean flag_quit= false; // control quit
    public static int hot=0;
    String start_date ,end_date;
    TextView tv_position,tv_times,tv_satellites,tv_used_time,tv_ratio1,tv_ratio2,tv_ratio3,tv_nema,tv_timeout,tv_testdata,tv_total_time;
    String Latitude,Longitude;
    String nema_info;
    int count; //satellites number
    SparseArray<Float> mySparseArray = new SparseArray();

    String times_st; // string iterate
    int i=0; //int iterate

    int timeout_times=0; // time out iterate
    String timeout_times_st; //time out String iterate;


    FileUtils mFileutil = new FileUtils();
    FileIoUtils mFileIOutils = new FileIoUtils();

    String fileName;
    String SD_path="/sdcard/";
    String mkName1="GPS/";
    String mkName2="Hot/";
    String private_filename="Hot_test.txt"; //test statistics

    String fileName_nema;
    String private_filename_nema="Hot_nema.txt"; //nema statistics

    private long mExitTime;
    Bundle bundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_hot);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(GPS_hot.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GPS_hot.this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);

            }
            // if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            //        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限

            //  }
        }


      /*  Bundle bundle = new Bundle();
       // locationManager.sendExtraCommand("gps", "delete_aiding_data", bundle); //冷启动
        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,"delete_aiding_data", null);


        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);//低精度，如果设置为高精度，依然获取不了location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        //从可用的位置提供器中，匹配以上标准的最佳提供器
        locationProvider = locationManager.getBestProvider(criteria, true);
        //Location location = locationManager.getLastKnownLocation(locationProvider);
       // Log.d(TAG, "onCreate: " + (location == null) + "..");
        //if (location != null) {
         //   Log.d(TAG, "onCreate: location");
         //   //不为空,显示地理位置经纬度
         //   showLocation(location);
     //   }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        locationManager.addGpsStatusListener(gpsStatusListener);
        time_start = System.currentTimeMillis();
        Log.d(TAG, "onCreate: start time"+time_start);*/
        Init();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        GPS_hot_Thread hot_thread = new GPS_hot_Thread();
        hot_thread.start();

    }
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    };    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 2;

    public void Init(){
        tv_position=findViewById(R.id.tv_position);
        tv_times=findViewById(R.id.tv_times);
        tv_satellites=findViewById(R.id.tv_satellite);
        tv_used_time=findViewById(R.id.tv_used_time);
        tv_ratio1=findViewById(R.id.tv_ratio1);
        tv_ratio2=findViewById(R.id.tv_ratio2);
        tv_ratio3=findViewById(R.id.tv_ratio3);
        tv_nema=findViewById(R.id.tv_nema);
        tv_timeout=findViewById(R.id.tv_timeout);
        tv_total_time=findViewById(R.id.tv_total_time);

        fileName = mFileutil.setFileName(private_filename);
        mFileIOutils.makeFilePath(SD_path,mkName1,mkName2,fileName);
        mFileutil.file_head(SD_path+mkName1+mkName2+fileName);

        fileName_nema = mFileutil.setFileName(private_filename_nema);
        mFileIOutils.makeFilePath(SD_path,mkName1,mkName2,fileName_nema);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider + ".." + Thread.currentThread().getName());
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider + ".." + Thread.currentThread().getName());
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + ".." + Thread.currentThread().getName());
            //如果位置发生变化,重新显示
            showLocation(location);
        }
    };

    private void showLocation(Location location) {
        if(!flag_send_msg_listener){
            Log.d(TAG,"定位成功------->"+"location------>纬度为：" + location.getLatitude() + "\n经度为" + location.getLongitude());
            time_end = System.currentTimeMillis();
            Log.d(TAG, "showLocation: end time "+time_end);
            diff_time=time_end-time_start;
            sum_time_foraverage+=diff_time;
            Log.d(TAG, "showLocation: diff"+diff_time);
            Latitude= String.valueOf(location.getLatitude());
            Longitude= String.valueOf(location.getLongitude());
            i++;
            Message msg = new Message();
            msg.what=0x3001;
            mHandler.sendMessage(msg);
            locationManager.removeUpdates(locationListener);
            locationManager.removeNmeaListener(mNmeaListener);
            locationManager.removeGpsStatusListener(gpsStatusListener);
            //locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,"delete_aiding_data", null);
            flag_gps=true;
            flag_send_msg_listener=true;
            flag_addlistener=false;
            hot=2;
        }else {
            locationManager.removeUpdates(locationListener);
            locationManager.removeNmeaListener(mNmeaListener);
            locationManager.removeGpsStatusListener(gpsStatusListener);
            Log.d(TAG, "showLocation: success change flag");
            hot=2;
        }


    }

    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "onGpsStatusChanged: first fix");
                    //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //获取当前状态
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                   // Log.d(TAG, "onGpsStatusChanged:maxSatellites "+maxSatellites);
                    //获取所有的卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    //卫星颗数统计
                    count=0;
                    StringBuilder sb = new StringBuilder();
                    while (iters.hasNext() && count <= maxSatellites) {
                        count++;

                        GpsSatellite s = iters.next();
                        //卫星的信噪比
                        float snr = s.getSnr();
                        mySparseArray.put(count,snr);
                        //Log.d(TAG, "onGpsStatusChanged: "+mySparseArray.get(count));
                        //sb.append("第").append(count).append("颗").append("：").append(snr).append("\n");
                    }


                    Message msg = new Message();
                    msg.what=0x3002;
                    mHandler.sendMessage(msg);
                   // Log.d(TAG, "onGpsStatusChanged: conut"+count);
                    Log.e("TAG", sb.toString());
                    break;
                default:
                    break;
            }
        }
    };

    private GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nema) {
            //System.out.println(nmea + "\n");
            Log.d(TAG, "onNmeaReceived: "+nema);
            mFileutil.result_append(SD_path+mkName1+mkName2+fileName_nema,mFileutil.getDate()+nema+"\t\n");
            nema_info=nema;
            Message msg = new Message();
            msg.what=0x3003;
            mHandler.sendMessage(msg);
            if(!flag_hot){
                locationManager.removeUpdates(locationListener);
                locationManager.removeNmeaListener(mNmeaListener);
                locationManager.removeGpsStatusListener(gpsStatusListener);
                Log.d(TAG, "onNmeaReceived: nema");
            }
        }
    };

    class GPS_hot_Thread extends Thread{
        public void run(){
            start_date=mFileutil.getDate();
            time_start_forsum=System.currentTimeMillis();
            while (true){
                if(flag_hot){

                    bundle.putBoolean("all",false);
                    //locationManager.sendExtraCommand("gps", "delete_aiding_data", bundle); //冷启动
                    locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                    locationManager.sendExtraCommand("gps", "force_xtra_injection", bundle);
                     //Log.d(TAG, "run: asdfasdfasdfasdssssssssssssssssssssssssssssssssssssss");
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
                    criteria.setAltitudeRequired(false);//不要求海拔
                    criteria.setBearingRequired(false);//不要求方位
                    criteria.setCostAllowed(true);//允许有花费
                    criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

                    //从可用的位置提供器中，匹配以上标准的最佳提供器
                    locationProvider = locationManager.getBestProvider(criteria, true);
                    //Location location = locationManager.getLastKnownLocation(locationProvider);
                    // Log.d(TAG, "onCreate: " + (location == null) + "..");
                    //if (location != null) {
                    //   Log.d(TAG, "onCreate: location");
                    //   //不为空,显示地理位置经纬度
                    //   showLocation(location);
                    //   }
                    //监视地理位置变化
                    try {
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    time_now = System.currentTimeMillis();
                    if(!flag_time_start){
                        Log.d(TAG, "run: time start");
                        Message message = new Message();
                        message.what = 0x3000;
                        mHandler.sendMessage(message);
                    }else if((!flag_gps)&&(time_now-time_start>180000)){
                        i++;
                        timeout_times++;
                        Message msg = new Message();
                        msg.what=0x3004;
                        mHandler.sendMessage(msg);
                        Log.d(TAG, "run: timeout 180 S");
                        locationManager.removeUpdates(locationListener);
                        locationManager.removeNmeaListener(mNmeaListener);
                        locationManager.removeGpsStatusListener(gpsStatusListener);
                        flag_send_msg_listener=false;
                        flag_time_start=false;
                        flag_gps=false;
                    }else if(flag_send_msg_listener){
                        Log.d(TAG, "run: success");
                        locationManager.removeUpdates(locationListener);
                        locationManager.removeNmeaListener(mNmeaListener);
                        locationManager.removeGpsStatusListener(gpsStatusListener);
                        flag_send_msg_listener=false;
                        flag_time_start=false;
                        flag_gps=false;
                    }

                }else {return;}
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x3000) {
                if(!flag_addlistener&&flag_hot) {
                    locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                    locationManager.addGpsStatusListener(gpsStatusListener);
                    locationManager.addNmeaListener(mNmeaListener);
                    time_start = System.currentTimeMillis();
                    Log.d(TAG, "onCreate: start time" + time_start);
                    flag_time_start = true;
                    flag_addlistener=true;
                    hot=1;
                }else{
                    locationManager.removeUpdates(locationListener);
                    locationManager.removeNmeaListener(mNmeaListener);
                    locationManager.removeGpsStatusListener(gpsStatusListener);
                    Log.d(TAG, "handleMessage: already listener");
                }
            }
            if (msg.what == 0x3001) {
                time_for_total=System.currentTimeMillis();
                sum_time=time_for_total-time_start_forsum;
                DecimalFormat df = new DecimalFormat("#.0000");
                Double sum_time_decimal= Double.valueOf(sum_time/3600000d);
                Double sum_time_decimal_4= Double.valueOf(df.format(sum_time_decimal));
                String sum_time_decimal_4_st = String.valueOf(sum_time_decimal_4);
                tv_total_time.setText(sum_time_decimal_4_st+" hours");

                String diff_time_st = String.valueOf(diff_time);
                tv_used_time.setText(diff_time_st+" ms");
                times_st= String.valueOf(i);
                tv_times.setText(times_st);
                tv_position.setText("纬度："+Latitude+"\n"+"经度:"+Longitude);
                mFileutil.result_append(SD_path+mkName1+mkName2+fileName,mFileutil.getDate()+"\t"+"*GPS Hot Boot Up*"+"\t"+times_st+"\t"+diff_time_st+" ms"+"\t"+Longitude+"#"+Latitude+"\t"+count+"\t"+"PASS"+"\t\n");
                Log.d(TAG, "handleMessage:total times"+i);
            }
            if (msg.what == 0x3002) {
                String satellite_number_st = String.valueOf(count);
                tv_satellites.setText(satellite_number_st);
                String ratio1_st = String.valueOf(mySparseArray.get(1));
                String ratio2_st = String.valueOf(mySparseArray.get(2));
                String ratio3_st = String.valueOf(mySparseArray.get(3));
                tv_ratio1.setText(ratio1_st);
                tv_ratio2.setText(ratio2_st);
                tv_ratio3.setText(ratio3_st);
            }if(msg.what==0x3003){
                tv_nema.setText(nema_info);
            }
            if(msg.what==0x3004){
                time_for_total=System.currentTimeMillis();
                sum_time=time_for_total-time_start_forsum;
                DecimalFormat df = new DecimalFormat("#.0000");
                Double sum_time_decimal= Double.valueOf(sum_time/3600000d);
                Double sum_time_decimal_4= Double.valueOf(df.format(sum_time_decimal));
                String sum_time_decimal_4_st = String.valueOf(sum_time_decimal_4);
                tv_total_time.setText(sum_time_decimal_4_st+" hours");

                times_st= String.valueOf(i);
                tv_times.setText(times_st);
                timeout_times_st= String.valueOf(timeout_times);
                tv_timeout.setText(timeout_times_st);
                mFileutil.result_append(SD_path+mkName1+mkName2+fileName,mFileutil.getDate()+"\t"+"*GPS Hot Boot Up*"+"\t"+times_st+"\t"+"180000"+"\t"+"fail"+"#"+"fail"+"\t"+count+"\t"+"Time Out:Exceeded 180s"+"\t\n");
                flag_addlistener=false;
                long time_out=180000;
                sum_time_foraverage+=time_out;
            }
            if(msg.what==0x3005){
                if(i==0){
                    mFileutil.result_append(SD_path+mkName1+mkName2+fileName,"Data Statistics："+"\n"+"["+start_date+"]"+"--"+"["+end_date+"]"+"\t\n"+"You test less than 1 ,not to statistics "+"\t\n");
                    flag_addlistener=false;
                }else if(i>0){
                    sum_time=time_end_forsum-time_start_forsum;
                    time_average=sum_time_foraverage/i/1000d;
                    DecimalFormat df = new DecimalFormat("#.0000");
                    Double sum_time_decimal= Double.valueOf(sum_time/3600000d);
                    Double sum_time_decimal_4= Double.valueOf(df.format(sum_time_decimal));
                    Log.d(TAG, "handleMessage: decimal"+sum_time_decimal);
                    String time_average_st = String.valueOf(time_average);
                    mFileutil.result_append(SD_path+mkName1+mkName2+fileName,"Data Statistics: "+"\n"+"["+start_date+"]"+"--"+"["+end_date+"]"+"\t\n"+"Test Duration:"+"\t"+sum_time_decimal_4+" hours"+"\t\n"+"Test Times: "+"\t"+i+"\n"+"Test Average Time: "+"\t"+time_average_st+" s"+"\t\n"+"Time Out:Exceeded 180s Times: "+"\t"+timeout_times_st+"\t\n");
                    flag_addlistener=false;
                    Toast.makeText(GPS_hot.this,"数据统计成功，请按返回键退出",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!flag_quit){
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                exit();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            View view;
            AlertDialog.Builder builder= new AlertDialog.Builder(GPS_hot.this);
            view= LayoutInflater.from(GPS_hot.this).inflate(R.layout.dialog, null);
            TextView cancel =view.findViewById(R.id.dialog_cancel);
            TextView sure =view.findViewById(R.id.dialog_sure);
            tv_testdata=view.findViewById(R.id.tv_testdata);
            tv_testdata.setText("\n"+SD_path+mkName1+mkName2+fileName+"\n\n"+SD_path+mkName1+mkName2+fileName_nema);
            final Dialog dialog= builder.create();
            dialog.show();
            dialog.getWindow().setContentView(view);
            cancel.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: cancel");
                    dialog.dismiss();
                }
            });
            sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: sure");
                    //locationProvider=null;
                    locationManager.removeUpdates(locationListener);
                    locationManager.removeNmeaListener(mNmeaListener);
                    locationManager.removeGpsStatusListener(gpsStatusListener);
                    Location location=null;
                    flag_hot=false;  //出现过走到destroy，但没有用........
                    time_end_forsum=System.currentTimeMillis();
                    end_date=mFileutil.getDate();
                    //finish();
                    dialog.dismiss();
                    flag_quit=true;
                    Message msg = new Message();
                    msg.what=0x3005;
                    mHandler.sendMessage(msg);
                }

            });
            mExitTime = System.currentTimeMillis();
        }
    }


    protected void onDestroy(){
       //Log.d(TAG, "onDestroy: hehehehe");
        super.onDestroy();
        //locationProvider=null;
        locationManager.removeUpdates(locationListener);
        locationManager.removeNmeaListener(mNmeaListener);
        locationManager.removeGpsStatusListener(gpsStatusListener);
        Location location=null;
        flag_hot=false;
    }

}
