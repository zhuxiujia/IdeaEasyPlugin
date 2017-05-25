package com.zxj.plugin.util;

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

}
