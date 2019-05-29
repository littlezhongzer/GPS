package com.example.quectel.quectel_gps;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

import static com.example.quectel.quectel_gps.GPS_cold.cold;
import static com.example.quectel.quectel_gps.GPS_hot.hot;
import static com.example.quectel.quectel_gps.GPS_warm.warm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    Intent intent = new Intent();
    Button btn_cold, btn_hot,btn_warm;
    Context context;
    public static boolean flag_hot=false;
    public static boolean flag_cold=false;
    public static boolean flag_warm=false;
    public static boolean flag_app_logcat_prc=true;

    Logcat_Class logcat = new Logcat_Class();

    PowerManager.WakeLock wakeLock;
    private long mExitTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);

            }
        }
        wakeLock=((PowerManager)getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PostLocationService");
        Log.d(TAG, "onCreate: wakelock"+wakeLock);
        initView();
        logcat.start();
        if(wakeLock!=null)
        {
            Log.d(TAG, "onCreate: wakelock start");
            wakeLock.acquire();//这句执行后，手机将不会休眠，直到执行wakeLock.release();方法
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //showAlterDialog_mainAC();

    }


    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.READ_PHONE_STATE
    };    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 2;


    public void initView() {
        btn_cold = findViewById(R.id.btn_cold);
        btn_cold.setOnClickListener(this);
        btn_hot = findViewById(R.id.btn_hot);
        btn_hot.setOnClickListener(this);
        btn_warm=findViewById(R.id.btn_warm);
        btn_warm.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cold:
                //if(warm==1|hot==1|cold==1){
                    //Toast.makeText(MainActivity.this,"请等待上次定位结束",Toast.LENGTH_SHORT).show();
                //}else {
                    flag_cold=true;
                    intent.setClass(MainActivity.this, GPS_cold.class);
                    startActivity(intent);
                    //Bundle bundle = new Bundle();
                    //locationManager.sendExtraCommand("gps", "delete_aiding_data", bundle); //冷启动
                    //locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,"delete_aiding_data", null);
                    break;
              //  }

            case R.id.btn_hot:
               // if(warm==1|hot==1|cold==1){
              //      Toast.makeText(MainActivity.this,"请等待上次定位结束",Toast.LENGTH_SHORT).show();
               // }else {
                    flag_hot=true;
                    intent.setClass(MainActivity.this, GPS_hot.class);
                    startActivity(intent);
                    break;
             //   }

            case R.id.btn_warm:
             //   if(warm==1|hot==1|cold==1){
             //       Toast.makeText(MainActivity.this,"请等待上次定位结束",Toast.LENGTH_SHORT).show();
             //       break;
             //   }else{
                    flag_warm=true;
                    intent.setClass(MainActivity.this,GPS_warm.class);
                    startActivity(intent);
                    break;
                }

        }

  //  }


    private void showAlterDialog_mainAC(){
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(MainActivity.this);
        alterDiaglog.setTitle("提示！！");//文字
        alterDiaglog.setMessage("测试报告路径："+"/sdcard/"+"\n\n"+"文件夹名\n"+"\tGPS冷启动："+"gps_cold_stress"+"\n"+"\tGPS温启动："+"gps_warm_stress"+"\n"+"\tGPS热启动："+"gps_hot_stress"+"\n\n"+"Nema文件夹名"+"\n\tGPS冷启动："+"gps_cold_nema"+"\n\t"+"GPS温启动："+"gps_warm_nema\n\t"+"GPS热启动："+"gps_hot_nema"+"\n\n"+"日志"+"\n"+"\tLogcat："+"gps_logcat.txt");//提示消息
        //积极的选择
        alterDiaglog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //显示
        alterDiaglog.show();
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag_app_logcat_prc=false;
        if(wakeLock!=null)
        {
            wakeLock.release();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


}
