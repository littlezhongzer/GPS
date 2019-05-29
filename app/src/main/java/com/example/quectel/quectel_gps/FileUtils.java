package com.example.quectel.quectel_gps;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by loyal.zhong on 2019/4/1.
 */

public class FileUtils {
    Calendar cal;
    String year,month,day,hour,minute,second,fileName;
    FileIoUtils mFileIOutils = new FileIoUtils();

    /**
     * 将输入流写入文件
     *
     *
     * @return 返回带时间戳的文件名称
     */
    public String setFileName(String name){
        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH)+1);
        day = String.valueOf(cal.get(Calendar.DATE));
        hour =  String.valueOf(cal.get(Calendar.HOUR));
        minute = String.valueOf(cal.get(Calendar.MINUTE));
        second = String.valueOf(cal.get(Calendar.SECOND));
        String date=year+"-"+month+"-"+day+"-"+hour+"-"+minute+"-"+second;
        fileName=date+"-"+name;
        return fileName;
    }

    /**
     * 将输入流写入文件
     *
     *
     * @return 返回时间戳
     */
    public String getDate(){
        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH)+1);
        day = String.valueOf(cal.get(Calendar.DATE));
        hour =  String.valueOf(cal.get(Calendar.HOUR));
        minute = String.valueOf(cal.get(Calendar.MINUTE));
        second = String.valueOf(cal.get(Calendar.SECOND));
        String Date_str=year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
        return  Date_str;
    }




    /**
     * 将输入流写入文件
     *
     *
     * @return  文件头
     */
    public void file_head(String filepath){
        mFileIOutils.writeFileFromString(filepath,"*Time*"+"\t"+"*Test Items*"+"\t"+"*Test Times*"+"\t"+"*Positioning Time*"+"\t"+"*Location*"+"\t"+"*Search Satellites*"+"\t"+"*Test Results*"+"\t\n",true);
    }




    /**
     * 将输入流写入文件
     *
     *
     * @return 写入结果
     */
    public void result_append(String filepath,String result){
        mFileIOutils.writeFileFromString(filepath,result,true);
    }
}
