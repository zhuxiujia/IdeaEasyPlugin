package com.zxj.plugin.util;

import java.io.*;

/**
 * Created by zhuxiujie on 2017/5/25.
 */
public class StringUtil {

    public static  String upFirstChar(String s){
        if(s!=null&&s.length()!=0){
            String f=s.substring(0,1);
            f=f.toUpperCase();
            s=f+s.substring(1);
            return s;
        }
        return null;
    }

    public static void writeStrToFile(String xml,String file){
        try {
            FileOutputStream fos = new FileOutputStream(new File(file));
            Writer os = new OutputStreamWriter(fos, "UTF-8");
            os.write(xml);
            os.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
