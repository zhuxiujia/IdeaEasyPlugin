package com.zxj.plugin.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by zhuxiujie
 */
public class PropertyUtil {

    public static void read(Properties prop,String fileName)throws Exception{
        //读取属性文件a.properties
        InputStream in = new BufferedInputStream(new FileInputStream(fileName));
        prop.load(in);     ///加载属性列表
        Iterator<String> it=prop.stringPropertyNames().iterator();
        while(it.hasNext()){
            String key=it.next();
            System.out.println(key+":"+prop.getProperty(key));
        }
        in.close();
    }

    public static void store(Properties properties,String fileName)throws Exception{
        ///保存属性到b.properties文件
        FileOutputStream oFile = new FileOutputStream(fileName, false);//true表示追加打开
        properties.store(oFile, "The New properties file");
        oFile.close();
    }
}
